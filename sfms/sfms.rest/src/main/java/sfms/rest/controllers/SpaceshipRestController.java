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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSpaceshipField;
import sfms.rest.CreateResult;
import sfms.rest.DeleteResult;
import sfms.rest.RestFactory;
import sfms.rest.SearchResult;
import sfms.rest.Throttle;
import sfms.rest.UpdateResult;
import sfms.rest.models.Spaceship;

@RestController
@RequestMapping("/spaceship")
public class SpaceshipRestController {

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Spaceship get(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.Spaceship.getKind())
				.newKey(Long.parseLong(id));

		Entity entity = datastore.get(key);

		RestFactory factory = new RestFactory();
		Spaceship result = factory.createSpaceship(entity);

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<Spaceship> search(
			@RequestParam("bookmark") Optional<String> bookmark,
			@RequestParam("pageIndex") Optional<Long> pageIndex,
			@RequestParam("pageSize") Optional<Long> pageSize,
			@RequestParam("filter") Optional<String> filter,
			@RequestParam("sort") Optional<String> sort) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(DbEntity.Spaceship.getKind())
				.build();

		QueryResults<Entity> entities = datastore.run(query);

		RestFactory factory = new RestFactory();
		List<Spaceship> spaceships = factory.createSpaceships(entities);

		SearchResult<Spaceship> result = new SearchResult<Spaceship>();
		result.setEntities(spaceships);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> update(@PathVariable String id, @RequestBody Spaceship spaceship) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.Spaceship.getKind())
				.newKey(Long.parseLong(id));

		Entity entity = Entity.newBuilder(key)
				.set(DbSpaceshipField.Name.getName(), spaceship.getName())
				.build();

		datastore.update(entity);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> create(@RequestBody Spaceship spaceship) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		IncompleteKey incompleteKey = datastore.newKeyFactory()
				.setKind(DbEntity.Spaceship.getKind())
				.newKey();
		Key key = datastore.allocateId(incompleteKey);

		Entity entity = Entity.newBuilder(key)
				.set(DbSpaceshipField.Name.getName(), spaceship.getName())
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
				.setKind(DbEntity.Spaceship.getKind())
				.newKey(Long.parseLong(id));

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}
