package sfms.rest.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.IOUtils;
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
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;
import sfms.rest.db.schemas.DbStarField;
import sfms.storage.Storage;
import sfms.storage.StorageManager;
import sfms.storage.StorageManagerUtility;
import sfms.storage.StorageManagerUtility.ObjectFactory;

@RestController
@RequestMapping("/sector")
public class SectorRestController {

	private final Logger logger = Logger.getLogger(SectorRestController.class.getName());

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
	public void getLookup(
			@PathVariable String id,
			HttpServletResponse response) throws Exception {

		logger.info("getLookup: start");

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		try (ReadableByteChannel readChannel = retrieveCachedSectorDataFromDb(id);
				InputStream inputStream = Channels.newInputStream(readChannel);) {
			response.setContentType("application/json");
			try (OutputStream outputStream = response.getOutputStream()) {
				IOUtils.copy(inputStream, outputStream);
			}
		}
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
		List<Sector> sectors = factory.createSectors(entities);

		SearchResult<Sector> result = new SearchResult<Sector>();
		result.setEntities(sectors);
		result.setEndingBookmark(entities.getCursorAfter().toUrlSafe());
		result.setEndOfResults(sectors.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> putUpdate(
			@PathVariable String id,
			@RequestBody Sector sector) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Sector.createEntityKey(datastore, id);

		Entity entity = Entity.newBuilder(key)
				.set(DbSectorField.MinimumX.getName(), sector.getMinimumX())
				.set(DbSectorField.MinimumY.getName(), sector.getMinimumY())
				.set(DbSectorField.MinimumZ.getName(), sector.getMinimumZ())
				.set(DbSectorField.MaximumX.getName(), sector.getMaximumX())
				.set(DbSectorField.MaximumY.getName(), sector.getMaximumY())
				.set(DbSectorField.MaximumZ.getName(), sector.getMaximumZ())
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
				.set(DbSectorField.MinimumX.getName(), sector.getMinimumX())
				.set(DbSectorField.MinimumY.getName(), sector.getMinimumY())
				.set(DbSectorField.MinimumZ.getName(), sector.getMinimumZ())
				.set(DbSectorField.MaximumX.getName(), sector.getMaximumX())
				.set(DbSectorField.MaximumY.getName(), sector.getMaximumY())
				.set(DbSectorField.MaximumZ.getName(), sector.getMaximumZ())
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

	private ReadableByteChannel retrieveCachedSectorDataFromDb(String id) throws Exception {

		StorageManager storageManager = Storage.getManager();
		return StorageManagerUtility.getCachedObject(storageManager, "Sector-" + id, "application/json",
				new ObjectFactory() {
					@Override
					public byte[] createObject() throws Exception {
						Sector result = retrieveSectorFromDb(id);

						ObjectMapper mapper = new ObjectMapper();
						ObjectWriter writer = mapper.writerFor(Sector.class);
						byte[] buffer = writer.writeValueAsBytes(result);

						return buffer;
					}
				});
	}

	private Sector retrieveSectorFromDb(String id) {
		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key = DbEntity.Sector.createEntityKey(datastore, id);
		Entity dbSector = datastore.get(key);

		Query<ProjectionEntity> query = Query.newProjectionEntityQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.addProjection(DbStarField.X.getName())
				.addProjection(DbStarField.Y.getName())
				.addProjection(DbStarField.Z.getName())
				.setFilter(PropertyFilter.eq(DbStarField.SectorKey.getName(), key))
				.build();

		logger.info("getLookup: run query");

		QueryResults<ProjectionEntity> dbStars = datastore.run(query);

		logger.info("getLookup: build results");

		RestFactory factory = new RestFactory();
		Sector result = factory.createSector(dbSector, factory.createStarsFromProjection(dbStars));
		return result;
	}
}
