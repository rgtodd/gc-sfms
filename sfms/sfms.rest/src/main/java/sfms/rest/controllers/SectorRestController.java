package sfms.rest.controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import sfms.common.Constants;
import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestHeaders;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.SectorField;
import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueFactory;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;
import sfms.rest.db.schemas.DbStarField;
import sfms.storage.Storage;
import sfms.storage.StorageManagerUtility;
import sfms.storage.StorageManagerUtility.ObjectFactory;

/**
 * Controller for the Sector REST service.
 * 
 * Provides basic CRUD operations for Sector entities.
 *
 */
@RestController
@RequestMapping("/sector")
public class SectorRestController {

	private static final Map<String, DbFieldSchema> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, DbFieldSchema>();
		s_dbFieldMap.put(SectorField.SectorX.getName(), DbSectorField.SectorX);
		s_dbFieldMap.put(SectorField.SectorY.getName(), DbSectorField.SectorY);
		s_dbFieldMap.put(SectorField.SectorZ.getName(), DbSectorField.SectorZ);
		s_dbFieldMap.put(SectorField.MinimumX.getName(), DbSectorField.MinimumX);
		s_dbFieldMap.put(SectorField.MinimumY.getName(), DbSectorField.MinimumY);
		s_dbFieldMap.put(SectorField.MinimumZ.getName(), DbSectorField.MinimumZ);
		s_dbFieldMap.put(SectorField.MaximumX.getName(), DbSectorField.MaximumX);
		s_dbFieldMap.put(SectorField.MaximumY.getName(), DbSectorField.MaximumY);
		s_dbFieldMap.put(SectorField.MaximumZ.getName(), DbSectorField.MaximumZ);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;
	private static final String DETAIL_STAR = "star";

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{key}", headers = { RestHeaders.CACHE + "!=" + RestHeaders.CACHE_ENABLED })
	public Sector getLookupUncached(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		return retrieveSectorFromDb(key);
	}

	@GetMapping(value = "/{key}", headers = { RestHeaders.CACHE + "=" + RestHeaders.CACHE_ENABLED })
	public void getLookupCached(@PathVariable String key, HttpServletResponse response) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		ObjectFactory objectFactory = new ObjectFactory() {
			@Override
			public byte[] createObject() throws Exception {
				Sector result = retrieveSectorFromDb(key);

				ObjectMapper mapper = new ObjectMapper();
				ObjectWriter writer = mapper.writerFor(Sector.class);
				byte[] buffer = writer.writeValueAsBytes(result);

				return buffer;
			}
		};

		String objectName = "Sector-" + key;

		try (ReadableByteChannel readChannel = StorageManagerUtility.getCachedObject(Storage.getManager(), objectName,
				Constants.CONTENT_TYPE_JSON, objectFactory);
				InputStream inputStream = Channels.newInputStream(readChannel)) {
			response.setContentType(Constants.CONTENT_TYPE_JSON);
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
			@RequestParam(RestParameters.SORT) Optional<String> sort,
			@RequestParam(RestParameters.DETAIL) Optional<String> detail) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		int limit = Integer.min(pageSize.orElse(DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		RestQuery dbSectorQuery = RestQueryBuilder.newQueryBuilder(s_dbFieldMap)
				.setType(RestQueryBuilderType.ENTITY)
				.setKind(DbEntity.Sector.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setQueryFilter(filter)
				.setStartCursor(bookmark)
				.build();

		RestQueryResults dbSectors = dbSectorQuery.run(datastore);

		List<Sector> sectors;
		if (detail.isPresent() && detail.get().equals(DETAIL_STAR)) {
			sectors = getSectorsWithDetail(datastore, dbSectors);
		} else {
			RestFactory factory = new RestFactory();
			sectors = factory.createSectors(dbSectors);
		}

		SearchResult<Sector> result = new SearchResult<Sector>();
		result.setEntities(sectors);
		result.setEndingBookmark(dbSectors.getCursorAfter().toUrlSafe());
		result.setEndOfResults(sectors.size() < limit);

		return result;
	}

	@PutMapping(value = "/{key}")
	public UpdateResult<String> putUpdate(@PathVariable String key, @RequestBody Sector sector) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSectorKey = DbEntity.Sector.createEntityKey(datastore, key);

		Entity dbSector = createDbSector(sector, dbSectorKey);

		datastore.update(dbSector);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(DbEntity.Sector.createRestKey(dbSectorKey));

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> putCreate(@RequestBody Sector sector) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSectorKey = DbEntity.Sector.createEntityKey(datastore, sector.getKey());

		Entity dbSector = createDbSector(sector, dbSectorKey);

		datastore.put(dbSector);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Sector.createRestKey(dbSectorKey));

		return result;
	}

	@DeleteMapping(value = "/{key}")
	public DeleteResult<String> delete(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSectorKey = DbEntity.Sector.createEntityKey(datastore, key);

		datastore.delete(dbSectorKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.Sector.createRestKey(dbSectorKey));

		return result;
	}

	private List<Sector> getSectorsWithDetail(Datastore datastore, RestQueryResults dbSectors) {

		RestFactory factory = new RestFactory();

		Long minimumX = null;
		Long maximumX = null;
		Long minimumY = null;
		Long maximumY = null;
		Long minimumZ = null;
		Long maximumZ = null;
		Map<String, Sector> sectorsByKey = new HashMap<String, Sector>();
		while (dbSectors.hasNext()) {
			BaseEntity<Key> dbSector = dbSectors.next();
			Sector sector = factory.createSector(dbSector, new ArrayList<Star>());
			if (minimumX == null || sector.getMinimumX() < minimumX) {
				minimumX = sector.getMinimumX();
			}
			if (maximumX == null || sector.getMaximumX() > maximumX) {
				maximumX = sector.getMaximumX();
			}
			if (minimumY == null || sector.getMinimumY() < minimumY) {
				minimumY = sector.getMinimumY();
			}
			if (maximumY == null || sector.getMaximumY() > maximumY) {
				maximumY = sector.getMaximumY();
			}
			if (minimumZ == null || sector.getMinimumZ() < minimumZ) {
				minimumZ = sector.getMinimumZ();
			}
			if (maximumZ == null || sector.getMaximumZ() > maximumZ) {
				maximumZ = sector.getMaximumZ();
			}
			sectorsByKey.put(sector.getKey(), sector);
		}

		if (!sectorsByKey.isEmpty()) {

			Query<ProjectionEntity> dbStarQuery = Query.newProjectionEntityQueryBuilder()
					.setKind(DbEntity.Star.getKind())
					.setFilter(CompositeFilter.and(
							PropertyFilter.ge(DbStarField.X.getName(), (double) minimumX),
							PropertyFilter.lt(DbStarField.X.getName(), (double) maximumX)))
					.addProjection(DbStarField.SectorKey.getName())
					.addProjection(DbStarField.X.getName())
					.addProjection(DbStarField.Y.getName())
					.addProjection(DbStarField.Z.getName())
					.build();

			QueryResults<ProjectionEntity> dbStars = datastore.run(dbStarQuery);

			while (dbStars.hasNext()) {
				BaseEntity<Key> dbStar = dbStars.next();
				double y = dbStar.getDouble(DbStarField.Y.getName());
				double z = dbStar.getDouble(DbStarField.Z.getName());
				if (minimumY <= y && y < maximumY && minimumZ <= z && z < maximumZ) {
					Star star = factory.createStar(dbStar);
					String starSectorKey = star.getSectorKey();
					if (starSectorKey != null) {
						Sector sector = sectorsByKey.get(starSectorKey);
						if (sector != null) {
							sector.getStars().add(star);
						}
					}
				}
			}
		}

		List<Sector> sectors = new ArrayList<Sector>(sectorsByKey.values());
		return sectors;
	}

	private Sector retrieveSectorFromDb(String id) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSectorKey = DbEntity.Sector.createEntityKey(datastore, id);

		Entity dbSector = datastore.get(dbSectorKey);

		Query<ProjectionEntity> dbStarQuery = Query.newProjectionEntityQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.addProjection(DbStarField.X.getName())
				.addProjection(DbStarField.Y.getName())
				.addProjection(DbStarField.Z.getName())
				.setFilter(PropertyFilter.eq(DbStarField.SectorKey.getName(), dbSectorKey))
				.build();

		QueryResults<ProjectionEntity> dbStars = datastore.run(dbStarQuery);

		RestFactory factory = new RestFactory();
		Sector sector = factory.createSector(dbSector, factory.createStarsFromProjection(dbStars));

		return sector;
	}

	private Entity createDbSector(Sector sector, Key dbSectorKey) {
		Entity dbSector = Entity.newBuilder(dbSectorKey)
				.set(DbSectorField.SectorX.getName(), DbValueFactory.asValue(sector.getSectorX()))
				.set(DbSectorField.SectorY.getName(), DbValueFactory.asValue(sector.getSectorY()))
				.set(DbSectorField.SectorZ.getName(), DbValueFactory.asValue(sector.getSectorZ()))
				.set(DbSectorField.MinimumX.getName(), DbValueFactory.asValue(sector.getMinimumX()))
				.set(DbSectorField.MinimumY.getName(), DbValueFactory.asValue(sector.getMinimumY()))
				.set(DbSectorField.MinimumZ.getName(), DbValueFactory.asValue(sector.getMinimumZ()))
				.set(DbSectorField.MaximumX.getName(), DbValueFactory.asValue(sector.getMaximumX()))
				.set(DbSectorField.MaximumY.getName(), DbValueFactory.asValue(sector.getMaximumY()))
				.set(DbSectorField.MaximumZ.getName(), DbValueFactory.asValue(sector.getMaximumZ()))
				.build();
		return dbSector;
	}
}
