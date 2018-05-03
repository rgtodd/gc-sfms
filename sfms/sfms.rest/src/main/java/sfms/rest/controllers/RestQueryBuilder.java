package sfms.rest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Value;

import sfms.rest.api.FilterCriteria;
import sfms.rest.api.SortCriteria;
import sfms.rest.db.DbFieldSchema;

public class RestQueryBuilder {

	private EntityQuery.Builder m_builder;
	private Map<String, DbFieldSchema> m_dbFieldMap;

	private RestQueryBuilder(EntityQuery.Builder builder, Map<String, DbFieldSchema> dbFieldMap) {
		m_builder = builder;
		m_dbFieldMap = dbFieldMap;
	}

	public static RestQueryBuilder newRestQueryBuilder(
			Map<String, DbFieldSchema> dbFieldMap) {
		return new RestQueryBuilder(Query.newEntityQueryBuilder(), dbFieldMap);
	}

	public RestQueryBuilder setKind(String kind) {
		m_builder.setKind(kind);
		return this;
	}

	public RestQueryBuilder setLimit(Integer limit) {
		m_builder.setLimit(limit);
		return this;
	}

	public RestQueryBuilder addSortCriteria(Optional<String> sort) {
		if (sort.isPresent()) {
			addSortCriteria(sort.get());
		}
		return this;
	}

	public RestQueryBuilder setQueryFilter(Optional<String> filter) {
		if (filter.isPresent()) {
			setQueryFilter(filter.get());
		}
		return this;
	}

	public RestQueryBuilder setStartCursor(Optional<String> bookmark) {
		if (bookmark.isPresent()) {
			m_builder.setStartCursor(Cursor.fromUrlSafe(bookmark.get()));
		}
		return this;
	}

	public EntityQuery build() {
		return m_builder.build();
	}

	private void addSortCriteria(String sort) {
		SortCriteria sortCriteria = SortCriteria.parse(sort);
		for (int idx = 0; idx < sortCriteria.size(); ++idx) {

			String restField = sortCriteria.getColumn(idx);
			DbFieldSchema dbField = m_dbFieldMap.get(restField);
			if (dbField != null) {

				if (sortCriteria.isDescending(idx)) {
					m_builder.addOrderBy(OrderBy.desc(dbField.getName()));
				} else {
					m_builder.addOrderBy(OrderBy.asc(dbField.getName()));
				}
			}
		}
	}

	private void setQueryFilter(String filter) {

		FilterCriteria filterCriteria = FilterCriteria.parse(filter);
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

		m_builder.setFilter(queryFilter);
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
