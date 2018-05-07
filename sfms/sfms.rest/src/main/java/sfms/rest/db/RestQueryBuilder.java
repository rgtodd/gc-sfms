package sfms.rest.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.ProjectionEntityQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Value;

import sfms.rest.api.FilterCriteria;
import sfms.rest.api.SelectionCriteria;
import sfms.rest.api.SortCriteria;

public class RestQueryBuilder {

	private Map<String, DbFieldSchema> m_dbFieldMap;
	private EntityQuery.Builder m_entityBuilder;
	private ProjectionEntityQuery.Builder m_projectionEntityBuilder;

	private RestQueryBuilder(Map<String, DbFieldSchema> dbFieldMap, EntityQuery.Builder entityBuilder) {
		m_dbFieldMap = dbFieldMap;
		m_entityBuilder = entityBuilder;
	}

	private RestQueryBuilder(Map<String, DbFieldSchema> dbFieldMap,
			ProjectionEntityQuery.Builder projectionEntityBuilder) {
		m_dbFieldMap = dbFieldMap;
		m_projectionEntityBuilder = projectionEntityBuilder;
	}

	public static RestQueryBuilder newEntityQueryBuilder(
			Map<String, DbFieldSchema> dbFieldMap) {
		return new RestQueryBuilder(dbFieldMap, Query.newEntityQueryBuilder());
	}

	public static RestQueryBuilder newProjectionEntityQueryBuilder(
			Map<String, DbFieldSchema> dbFieldMap) {
		return new RestQueryBuilder(dbFieldMap, Query.newProjectionEntityQueryBuilder());
	}

	public static RestQueryBuilder newQueryBuilder(
			Map<String, DbFieldSchema> dbFieldMap, Optional<String> selectionCriteria) {
		if (selectionCriteria.isPresent()) {
			return newProjectionEntityQueryBuilder(dbFieldMap).addSelectionCriteria(selectionCriteria);
		} else {
			return newEntityQueryBuilder(dbFieldMap);
		}
	}

	public static RestQueryBuilder newQueryBuilder(
			Map<String, DbFieldSchema> dbFieldMap, SelectionCriteria selectionCriteria) {
		if (selectionCriteria != null) {
			return newProjectionEntityQueryBuilder(dbFieldMap).addSelectionCriteria(selectionCriteria);
		} else {
			return newEntityQueryBuilder(dbFieldMap);
		}
	}

	public RestQueryBuilder setKind(String kind) {
		getBuilder().setKind(kind);
		return this;
	}

	public RestQueryBuilder setLimit(Integer limit) {
		getBuilder().setLimit(limit);
		return this;
	}

	public RestQueryBuilder addSelectionCriteria(Optional<String> selectionCriteria) {
		if (selectionCriteria.isPresent()) {
			return addSelectionCriteria(SelectionCriteria.parse(selectionCriteria.get()));
		}
		return this;
	}

	public RestQueryBuilder addSelectionCriteria(SelectionCriteria selectionCriteria) {
		if (selectionCriteria != null) {
			for (int idx = 0; idx < selectionCriteria.size(); ++idx) {
				String restField = selectionCriteria.getColumn(idx);
				DbFieldSchema dbField = m_dbFieldMap.get(restField);
				if (dbField != null) {
					m_projectionEntityBuilder.addProjection(dbField.getName());
				}
			}
		}
		return this;
	}

	public RestQueryBuilder addSortCriteria(Optional<String> sortCriteria) {
		if (sortCriteria.isPresent()) {
			return addSortCriteria(SortCriteria.parse(sortCriteria.get()));
		}
		return this;
	}

	public RestQueryBuilder addSortCriteria(SortCriteria sortCriteria) {
		if (sortCriteria != null) {
			for (int idx = 0; idx < sortCriteria.size(); ++idx) {

				String restField = sortCriteria.getColumn(idx);
				DbFieldSchema dbField = m_dbFieldMap.get(restField);
				if (dbField != null) {
					if (sortCriteria.isDescending(idx)) {
						getBuilder().addOrderBy(OrderBy.desc(dbField.getName()));
					} else {
						getBuilder().addOrderBy(OrderBy.asc(dbField.getName()));
					}
				}
			}
		}
		return this;
	}

	public RestQueryBuilder setFilterCriteria(Optional<String> filterCriteria) {
		if (filterCriteria.isPresent()) {
			return setFilterCriteria(FilterCriteria.parse(filterCriteria.get()));
		}
		return this;
	}

	public RestQueryBuilder setFilterCriteria(FilterCriteria filterCriteria) {
		if (filterCriteria != null) {
			Filter queryFilter;
			if (filterCriteria.size() == 1) {
				queryFilter = createColumnFilter(filterCriteria, 0);
			} else {
				Filter firstSubfilter = createColumnFilter(filterCriteria, 0);
				List<Filter> remainingSubfilters = new ArrayList<Filter>();
				for (int idx = 1; idx < filterCriteria.size(); ++idx) {
					remainingSubfilters.add(createColumnFilter(filterCriteria, idx));
				}
				queryFilter = CompositeFilter.and(firstSubfilter, remainingSubfilters.toArray(new Filter[0]));
			}

			getBuilder().setFilter(queryFilter);
		}
		return this;
	}

	public RestQueryBuilder setStartCursor(Optional<String> bookmark) {
		if (bookmark.isPresent()) {
			getBuilder().setStartCursor(Cursor.fromUrlSafe(bookmark.get()));
		}
		return this;
	}

	public RestQuery build() {
		if (m_entityBuilder != null) {
			return RestQuery.createEntityRestQuery(m_entityBuilder.build());
		} else {
			return RestQuery.createProjectionEntityRestQuery(m_projectionEntityBuilder.build());
		}
	}

	private StructuredQuery.Builder<?> getBuilder() {
		if (m_entityBuilder != null) {
			return m_entityBuilder;
		} else {
			return m_projectionEntityBuilder;
		}
	}

	private Filter createColumnFilter(FilterCriteria filterCriteria, int idx) {
		String column = filterCriteria.getColumn(idx);
		String operator = filterCriteria.getOperator(idx);
		String valueString = filterCriteria.getValue(idx);

		DbFieldSchema dbField = m_dbFieldMap.get(column);
		Value<?> value = dbField.parseValue(valueString);

		Filter currentFilter;
		switch (operator) {
		case FilterCriteria.EQ:
			currentFilter = PropertyFilter.eq(dbField.getName(), value);
			break;
		case FilterCriteria.LT:
			currentFilter = PropertyFilter.lt(dbField.getName(), value);
			break;
		case FilterCriteria.LE:
			currentFilter = PropertyFilter.le(dbField.getName(), value);
			break;
		case FilterCriteria.GT:
			currentFilter = PropertyFilter.gt(dbField.getName(), value);
			break;
		case FilterCriteria.GE:
			currentFilter = PropertyFilter.ge(dbField.getName(), value);
			break;
		default:
			currentFilter = null;
		}
		return currentFilter;
	}

}
