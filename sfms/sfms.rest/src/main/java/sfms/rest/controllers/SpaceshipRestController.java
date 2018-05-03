package sfms.rest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.schemas.SpaceshipField;
import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSpaceshipField;

/**
 * Controller for the Spaceship REST service.
 * 
 * Provides basic CRUD operations for Space Ship entities.
 * 
 */
@RestController
@RequestMapping("/spaceship")
public class SpaceshipRestController {

	private static final Map<String, DbFieldSchema> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, DbFieldSchema>();
		s_dbFieldMap.put(SpaceshipField.Name.getName(), DbSpaceshipField.Name);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Spaceship getLookup(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Spaceship.createEntityKey(datastore, id);

		Entity entity = datastore.get(key);

		RestFactory factory = new RestFactory();
		Spaceship result = factory.createSpaceship(entity);

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<Spaceship> getSearch(
			@RequestParam(RestParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(RestParameters.PAGE_INDEX) Optional<Long> pageIndex,
			@RequestParam(RestParameters.PAGE_SIZE) Optional<Integer> pageSize,
			@RequestParam(RestParameters.FILTER) Optional<String> filter,
			@RequestParam(RestParameters.SORT) Optional<String> sort,
			@RequestParam(RestParameters.DETAIL) Optional<String> detail) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		int limit = Integer.min(pageSize.orElse(DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = RestQueryBuilder.newRestQueryBuilder(s_dbFieldMap)
				.setKind(DbEntity.Spaceship.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setQueryFilter(filter)
				.setStartCursor(bookmark)
				.build();

		QueryResults<Entity> entities = datastore.run(query);

		RestFactory factory = new RestFactory();
		List<Spaceship> spaceships = factory.createSpaceships(entities);

		SearchResult<Spaceship> result = new SearchResult<Spaceship>();
		result.setEntities(spaceships);
		result.setEndingBookmark(entities.getCursorAfter().toUrlSafe());
		result.setEndOfResults(spaceships.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> putUpdate(@PathVariable String id, @RequestBody Spaceship spaceship) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Spaceship.createEntityKey(datastore, id);

		Entity entity = Entity.newBuilder(key).set(DbSpaceshipField.Name.getName(), spaceship.getName()).build();

		datastore.update(entity);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PostMapping(value = "")
	public CreateResult<String> postCreate(@RequestBody Spaceship spaceship) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		IncompleteKey incompleteKey = datastore.newKeyFactory().setKind(DbEntity.Spaceship.getKind()).newKey();

		FullEntity<IncompleteKey> entity = FullEntity.newBuilder(incompleteKey)
				.set(DbSpaceshipField.Name.getName(), spaceship.getName()).build();

		Entity createdEntity = datastore.put(entity);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Spaceship.createRestKey(createdEntity.getKey()));

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<String> delete(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Spaceship.createEntityKey(datastore, id);

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}
