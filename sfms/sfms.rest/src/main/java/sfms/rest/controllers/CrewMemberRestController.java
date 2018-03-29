package sfms.rest.controllers;

import java.util.List;
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

import sfms.rest.CreateResult;
import sfms.rest.DeleteResult;
import sfms.rest.RestFactory;
import sfms.rest.SearchResult;
import sfms.rest.Throttle;
import sfms.rest.UpdateResult;
import sfms.rest.models.CrewMember;
import sfms.rest.schemas.CrewMemberEntitySchema;

@RestController
@RequestMapping("/crewMember")
public class CrewMemberRestController {

	private static final int DEFAULT_PAGE_SIZE = 10;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public CrewMember get(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(CrewMemberEntitySchema.Kind)
				.newKey(Long.parseLong(id));

		Entity entity = datastore.get(key);

		RestFactory factory = new RestFactory();
		CrewMember result = factory.createCrewMember(entity);

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<CrewMember> search(
			@RequestParam("bookmark") Optional<String> bookmark,
			@RequestParam("pageIndex") Optional<Long> pageIndex,
			@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("filter") Optional<String> filter,
			@RequestParam("sort") Optional<String> sort) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		int limit = pageSize.orElse(DEFAULT_PAGE_SIZE);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Builder queryBuilder = Query.newEntityQueryBuilder();
		queryBuilder.setKind(CrewMemberEntitySchema.Kind);
		queryBuilder.setLimit(limit);
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
				.setKind(CrewMemberEntitySchema.Kind)
				.newKey(Long.parseLong(id));

		Entity entity = Entity.newBuilder(key)
				.set(CrewMemberEntitySchema.FirstName, crewMember.getFirstName())
				.set(CrewMemberEntitySchema.LastName, crewMember.getLastName())
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
				.setKind(CrewMemberEntitySchema.Kind)
				.newKey();
		Key key = datastore.allocateId(incompleteKey);

		Entity entity = Entity.newBuilder(key)
				.set(CrewMemberEntitySchema.FirstName, crewMember.getFirstName())
				.set(CrewMemberEntitySchema.LastName, crewMember.getLastName())
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
				.setKind(CrewMemberEntitySchema.Kind)
				.newKey(Long.parseLong(id));

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}
