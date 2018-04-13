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
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SortCriteria;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Sector;
import sfms.rest.api.schemas.SectorField;
import sfms.rest.db.schemas.DbSectorField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

@RestController
@RequestMapping("/sector")
public class SectorRestController {

	private static final Map<SectorField, DbSectorField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<SectorField, DbSectorField>();
		s_dbFieldMap.put(SectorField.MinimumX, DbSectorField.MinimumX);
		s_dbFieldMap.put(SectorField.MinimumY, DbSectorField.MinimumY);
		s_dbFieldMap.put(SectorField.MinimumZ, DbSectorField.MinimumZ);
		s_dbFieldMap.put(SectorField.MaximumX, DbSectorField.MaximumX);
		s_dbFieldMap.put(SectorField.MaximumY, DbSectorField.MaximumY);
		s_dbFieldMap.put(SectorField.MaximumZ, DbSectorField.MaximumZ);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Sector getLookup(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Sector.createEntityKey(datastore, id);
		Entity dbSector = datastore.get(key);

		Query<ProjectionEntity> query = Query.newProjectionEntityQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.addProjection(DbStarField.X.getId())
				.addProjection(DbStarField.Y.getId())
				.addProjection(DbStarField.Z.getId())
				.setFilter(PropertyFilter.eq(DbStarField.SectorKey.getId(), key))
				.build();

		QueryResults<ProjectionEntity> dbStars = datastore.run(query);

		RestFactory factory = new RestFactory();
		Sector result = factory.createSector(dbSector, factory.createStarsFromProjection(dbStars));

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<Sector> getSearch(
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
		queryBuilder.setKind(DbEntity.Sector.getKind());
		queryBuilder.setLimit(limit);
		if (sort.isPresent()) {
			SortCriteria sortCriteria = SortCriteria.parse(sort.get());
			for (int idx = 0; idx < sortCriteria.size(); ++idx) {
				SectorField restField = SectorField.parse(sortCriteria.getColumn(idx));
				DbSectorField dbField = s_dbFieldMap.get(restField);
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
		List<Sector> sectors = factory.createSectors(entities);

		SearchResult<Sector> result = new SearchResult<Sector>();
		result.setEntities(sectors);
		result.setEndingBookmark(entities.getCursorAfter().toUrlSafe());
		result.setEndOfResults(sectors.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> putUpdate(@PathVariable String id, @RequestBody Sector sector)
			throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Sector.createEntityKey(datastore, id);

		Entity entity = Entity.newBuilder(key)
				.set(DbSectorField.MinimumX.getId(), sector.getMinimumX())
				.set(DbSectorField.MinimumY.getId(), sector.getMinimumY())
				.set(DbSectorField.MinimumZ.getId(), sector.getMinimumZ())
				.set(DbSectorField.MaximumX.getId(), sector.getMaximumX())
				.set(DbSectorField.MaximumY.getId(), sector.getMaximumY())
				.set(DbSectorField.MaximumZ.getId(), sector.getMaximumZ())
				.build();

		datastore.update(entity);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> putCreate(@RequestBody Sector sector) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Sector.createEntityKey(datastore, sector.getKey());

		Entity entity = Entity.newBuilder(key)
				.set(DbSectorField.MinimumX.getId(), sector.getMinimumX())
				.set(DbSectorField.MinimumY.getId(), sector.getMinimumY())
				.set(DbSectorField.MinimumZ.getId(), sector.getMinimumZ())
				.set(DbSectorField.MaximumX.getId(), sector.getMaximumX())
				.set(DbSectorField.MaximumY.getId(), sector.getMaximumY())
				.set(DbSectorField.MaximumZ.getId(), sector.getMaximumZ())
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

		Key key = DbEntity.Sector.createEntityKey(datastore, id);

		datastore.delete(key);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(id);

		return result;
	}
}
