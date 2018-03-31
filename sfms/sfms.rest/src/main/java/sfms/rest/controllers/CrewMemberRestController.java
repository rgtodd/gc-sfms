package sfms.rest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import sfms.db.schemas.DbCrewMemberField;
import sfms.db.schemas.DbEntity;
import sfms.rest.CreateResult;
import sfms.rest.DeleteResult;
import sfms.rest.RestParameters;
import sfms.rest.RestFactory;
import sfms.rest.SearchResult;
import sfms.rest.SortCriteria;
import sfms.rest.Throttle;
import sfms.rest.UpdateResult;
import sfms.rest.models.CrewMember;
import sfms.rest.schemas.CrewMemberField;

@RestController
@RequestMapping("/crewMember")
public class CrewMemberRestController {

	private static final Map<CrewMemberField, DbCrewMemberField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<CrewMemberField, DbCrewMemberField>();
		s_dbFieldMap.put(CrewMemberField.FirstName, DbCrewMemberField.FirstName);
		s_dbFieldMap.put(CrewMemberField.LastName, DbCrewMemberField.LastName);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public CrewMember get(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.CrewMember.getKind())
				.newKey(Long.parseLong(id));

		Entity entity = datastore.get(key);

		RestFactory factory = new RestFactory();
		CrewMember result = factory.createCrewMember(entity);

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<CrewMember> search(
			@RequestParam(RestParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(RestParameters.PAGE_INDEX) Optional<Long> pageIndex,
			@RequestParam(RestParameters.PAGE_SIZE) Optional<Integer> pageSize,
			@RequestParam(RestParameters.FILTER) Optional<String> filter,
			@RequestParam(RestParameters.SORT) Optional<String> sort) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		int limit = Integer.min(pageSize.orElse(DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Builder queryBuilder = Query.newEntityQueryBuilder();
		queryBuilder.setKind(DbEntity.CrewMember.getKind());
		queryBuilder.setLimit(limit);
		if (sort.isPresent()) {
			SortCriteria sortCriteria = SortCriteria.parse(sort.get());
			for (int idx = 0; idx < sortCriteria.size(); ++idx) {
				CrewMemberField restField = CrewMemberField.parse(sortCriteria.getColumn(idx));
				DbCrewMemberField dbField = s_dbFieldMap.get(restField);
				if (dbField != null) {
					if (sortCriteria.getDescending(idx)) {
						queryBuilder.addOrderBy(OrderBy.desc(dbField.getName()));
					} else {
						queryBuilder.addOrderBy(OrderBy.asc(dbField.getName()));
					}
				}
			}
		}
		if (bookmark.isPresent()) {
			queryBuilder.setStartCursor(Cursor.fromUrlSafe(bookmark.get()));
		}

		Query<Entity> query = queryBuilder.build();

		QueryResults<Entity> entities = datastore.run(query);

		RestFactory factory = new RestFactory();
		List<CrewMember> crewMembers = factory.createCrewMembers(entities);

		SearchResult<CrewMember> result = new SearchResult<CrewMember>();
		result.setEntities(crewMembers);
		result.setEndingBookmark(entities.getCursorAfter().toUrlSafe());
		result.setEndOfResults(crewMembers.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> update(@PathVariable String id, @RequestBody CrewMember crewMember) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.CrewMember.getKind())
				.newKey(Long.parseLong(id));

		Entity entity = Entity.newBuilder(key)
				.set(DbCrewMemberField.FirstName.getName(), crewMember.getFirstName())
				.set(DbCrewMemberField.LastName.getName(), crewMember.getLastName())
				.build();

		datastore.update(entity);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> create(@RequestBody CrewMember crewMember) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		IncompleteKey incompleteKey = datastore.newKeyFactory()
				.setKind(DbEntity.CrewMember.getKind())
				.newKey();
		Key key = datastore.allocateId(incompleteKey);

		Entity entity = Entity.newBuilder(key)
				.set(DbCrewMemberField.FirstName.getName(), crewMember.getFirstName())
				.set(DbCrewMemberField.LastName.getName(), crewMember.getLastName())
				.build();

		datastore.put(entity);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(key.getId().toString());

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<String> delete(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.CrewMember.getKind())
				.newKey(Long.parseLong(id));

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}
