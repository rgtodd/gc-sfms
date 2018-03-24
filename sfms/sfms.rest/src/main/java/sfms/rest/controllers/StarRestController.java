package sfms.rest.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
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
import sfms.rest.models.Star;
import sfms.rest.schemas.StarEntitySchema;

@RestController
@RequestMapping("/star")
public class StarRestController {

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Star get(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore
				.newKeyFactory()
				.setKind(StarEntitySchema.Kind)
				.newKey(Long.parseLong(id));

		Entity entity = datastore.get(key);

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(entity);

		return star;
	}

	@GetMapping(value = "")
	public SearchResult<Star> search() throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(StarEntitySchema.Kind)
				.build();

		QueryResults<Entity> entities = datastore.run(query);

		RestFactory factory = new RestFactory();
		List<Star> stars = factory.createStars(entities);

		SearchResult<Star> result = new SearchResult<Star>();
		result.setEntities(stars);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> update(@PathVariable String id, @RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore
				.newKeyFactory()
				.setKind(StarEntitySchema.Kind)
				.newKey(Long.parseLong(id));

		Entity entity = Entity.newBuilder(key)
				.set(StarEntitySchema.StarId, star.getStarId())
				.set(StarEntitySchema.ProperName, star.getProperName())
				.build();

		datastore.update(entity);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> create(@RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		IncompleteKey incompleteKey = datastore
				.newKeyFactory()
				.setKind(StarEntitySchema.Kind)
				.newKey();
		Key key = datastore.allocateId(incompleteKey);

		Entity entity = Entity.newBuilder(key)
				.set(StarEntitySchema.StarId, star.getStarId())
				.set(StarEntitySchema.ProperName, star.getProperName())
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

		Key key = datastore
				.newKeyFactory()
				.setKind(StarEntitySchema.Kind)
				.newKey(Long.parseLong(id));

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}