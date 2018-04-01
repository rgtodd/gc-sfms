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
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Star;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

@RestController
@RequestMapping("/star")
public class StarRestController {

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Star get(@PathVariable long id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(id);

		Entity entity = datastore.get(key);

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(entity);

		return star;
	}

	@GetMapping(value = "")
	public SearchResult<Star> search(
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
				.setKind(DbEntity.Star.getKind())
				.build();

		QueryResults<Entity> entities = datastore.run(query);

		RestFactory factory = new RestFactory();
		List<Star> stars = factory.createStars(entities);

		SearchResult<Star> result = new SearchResult<Star>();
		result.setEntities(stars);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<Long> update(@PathVariable long id, @RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore
				.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(id);

		Entity entity = Entity.newBuilder(key)
				.set(DbStarField.ProperName.getName(), star.getProperName())
				.build();

		datastore.update(entity);

		UpdateResult<Long> result = new UpdateResult<Long>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<Long> create(@RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore
				.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(Long.parseLong(star.getKey()));

		Entity entity = Entity.newBuilder(key)
				.set(DbStarField.ProperName.getName(), star.getProperName())
				.build();

		datastore.put(entity);

		CreateResult<Long> result = new CreateResult<Long>();
		result.setKey(key.getId());

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<Long> delete(@PathVariable long id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore
				.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(id);

		datastore.delete(key);

		DeleteResult<Long> result = new DeleteResult<Long>();
		result.setKey(id);

		return result;
	}
}
