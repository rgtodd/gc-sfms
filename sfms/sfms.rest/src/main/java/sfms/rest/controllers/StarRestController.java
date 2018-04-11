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
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SortCriteria;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.CrewMemberField;
import sfms.rest.api.schemas.StarField;
import sfms.rest.db.schemas.DbCrewMemberField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

@RestController
@RequestMapping("/star")
public class StarRestController {

	private static final Map<StarField, DbStarField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<StarField, DbStarField>();
		s_dbFieldMap.put(StarField.X, DbStarField.X);
		s_dbFieldMap.put(StarField.Y, DbStarField.Y);
		s_dbFieldMap.put(StarField.Z, DbStarField.Z);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Star getLookup(@PathVariable long id) throws Exception {

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
	public SearchResult<Star> getSearch(
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
		queryBuilder.setKind(DbEntity.Star.getKind());
		queryBuilder.setLimit(limit);
		if (sort.isPresent()) {
			SortCriteria sortCriteria = SortCriteria.parse(sort.get());
			for (int idx = 0; idx < sortCriteria.size(); ++idx) {
				StarField restField = StarField.parse(sortCriteria.getColumn(idx));
				DbStarField dbField = s_dbFieldMap.get(restField);
				if (dbField != null) {
					if (sortCriteria.getDescending(idx)) {
						queryBuilder.addOrderBy(OrderBy.desc(dbField.getId()));
					} else {
						queryBuilder.addOrderBy(OrderBy.asc(dbField.getId()));
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
		List<Star> stars = factory.createStars(entities);

		SearchResult<Star> result = new SearchResult<Star>();
		result.setEntities(stars);
		result.setEndingBookmark(entities.getCursorAfter().toUrlSafe());
		result.setEndOfResults(stars.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> putUpdate(@PathVariable String id, @RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(Long.parseLong(id));

		Entity entity = Entity.newBuilder(key)
				.set(DbStarField.ProperName.getId(), star.getProperName())
				.build();

		datastore.update(entity);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> putCreate(@RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = datastore.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(Long.parseLong(star.getKey()));

		Entity entity = Entity.newBuilder(key)
				.set(DbStarField.ProperName.getId(), star.getProperName())
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
				.setKind(DbEntity.Star.getKind())
				.newKey(Long.parseLong(id));

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}
