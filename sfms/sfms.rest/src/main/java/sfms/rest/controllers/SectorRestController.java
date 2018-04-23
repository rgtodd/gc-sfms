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
import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Value;

import sfms.common.Constants;
import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.FilterCriteria;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SortCriteria;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.SectorField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;
import sfms.rest.db.schemas.DbStarField;
import sfms.storage.Storage;
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

	private static final int DEFAULT_PAGE_SIZE = 1000;
	private static final int MAX_PAGE_SIZE = 1000;
	private static final String DETAIL_STAR = "star";

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/get/{id}")
	public Sector getLookup(@PathVariable String id) throws Exception {

		logger.info("getLookup: start");

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		return retrieveSectorFromDb(id);
	}

	@GetMapping(value = "/{id}")
	public void getLookupCached(
			@PathVariable String id,
			HttpServletResponse response) throws Exception {

		logger.info("getLookupCached: start");

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		ObjectFactory objectFactory = new ObjectFactory() {
			@Override
			public byte[] createObject() throws Exception {
				Sector result = retrieveSectorFromDb(id);

				ObjectMapper mapper = new ObjectMapper();
				ObjectWriter writer = mapper.writerFor(Sector.class);
				byte[] buffer = writer.writeValueAsBytes(result);

				return buffer;
			}
		};

		String objectName = "Sector-" + id;

		try (ReadableByteChannel readChannel = StorageManagerUtility.getCachedObject(
				Storage.getManager(),
				objectName,
				Constants.CONTENT_TYPE_JSON,
				objectFactory);
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

		Builder sectorQueryBuilder = Query.newEntityQueryBuilder();
		sectorQueryBuilder.setKind(DbEntity.Sector.getKind());
		sectorQueryBuilder.setLimit(limit);
		if (sort.isPresent()) {
			addSortCriteria(sectorQueryBuilder, sort.get());
		}
		if (filter.isPresent()) {
			setQueryFilter(sectorQueryBuilder, filter.get());
		}
		if (bookmark.isPresent()) {
			sectorQueryBuilder.setStartCursor(Cursor.fromUrlSafe(bookmark.get()));
		}
		Query<Entity> sectorQuery = sectorQueryBuilder.build();

		QueryResults<Entity> dbSectors = datastore.run(sectorQuery);

		RestFactory factory = new RestFactory();
		List<Sector> sectors;
		if (detail.isPresent() && detail.get().equals(DETAIL_STAR)) {

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

				Query<ProjectionEntity> starQuery = Query.newProjectionEntityQueryBuilder()
						.setKind(DbEntity.Star.getKind())
						.setFilter(
								CompositeFilter.and(
										PropertyFilter.ge(DbStarField.X.getName(), (double) minimumX),
										PropertyFilter.lt(DbStarField.X.getName(), (double) maximumX)))
						.addProjection(DbStarField.SectorKey.getName())
						.addProjection(DbStarField.X.getName())
						.addProjection(DbStarField.Y.getName())
						.addProjection(DbStarField.Z.getName()).build();

				QueryResults<ProjectionEntity> dbStars = datastore.run(starQuery);

				while (dbStars.hasNext()) {
					BaseEntity<Key> dbStar = dbStars.next();
					double y = dbStar.getDouble(DbStarField.Y.getName());
					double z = dbStar.getDouble(DbStarField.Z.getName());
					if (y >= minimumY && y < maximumY && z >= minimumZ && z < maximumZ) {
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

			sectors = new ArrayList<Sector>(sectorsByKey.values());
		} else {
			sectors = factory.createSectors(dbSectors);
		}

		SearchResult<Sector> result = new SearchResult<Sector>();
		result.setEntities(sectors);
		result.setEndingBookmark(dbSectors.getCursorAfter().toUrlSafe());
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

	private void addSortCriteria(Builder queryBuilder, String sort) {
		SortCriteria sortCriteria = SortCriteria.parse(sort);
		for (int idx = 0; idx < sortCriteria.size(); ++idx) {

			SectorField restField = SectorField.parse(sortCriteria.getColumn(idx));
			DbSectorField dbField = s_dbFieldMap.get(restField);
			if (dbField != null) {

				if (sortCriteria.isDescending(idx)) {
					queryBuilder.addOrderBy(OrderBy.desc(dbField.getName()));
				} else {
					queryBuilder.addOrderBy(OrderBy.asc(dbField.getName()));
				}
			}
		}
	}

	private void setQueryFilter(Builder queryBuilder, String filter) {

		FilterCriteria filterCriteria = FilterCriteria.parse(filter);
		Filter queryFilter;
		if (filterCriteria.size() == 1) {
			queryFilter = createColumnFilter(filterCriteria, 0);
		} else {
			Filter firstSubfilter = createColumnFilter(filterCriteria, 0);
			List<Filter> remainingSubfilters = new ArrayList<Filter>();
			for (int idx = 1; idx < filterCriteria.size(); ++idx) {
				remainingSubfilters.add(createColumnFilter(filterCriteria, idx));
			}
			queryFilter = CompositeFilter.and(firstSubfilter, remainingSubfilters.toArray(new Filter[0]));
		}

		queryBuilder.setFilter(queryFilter);
	}

	private Filter createColumnFilter(FilterCriteria filterCriteria, int idx) {
		String column = filterCriteria.getColumn(idx);
		String operator = filterCriteria.getOperator(idx);
		String valueString = filterCriteria.getValue(idx);

		SectorField restField = SectorField.parse(column);
		DbSectorField dbField = s_dbFieldMap.get(restField);
		Value<?> value = dbField.parseValue(valueString);

		Filter currentFilter;
		switch (operator) {
		case FilterCriteria.EQ:
			currentFilter = PropertyFilter.eq(dbField.getName(), value);
			break;
		case FilterCriteria.LT:
			currentFilter = PropertyFilter.lt(dbField.getName(), value);
			break;
		case FilterCriteria.LE:
			currentFilter = PropertyFilter.le(dbField.getName(), value);
			break;
		case FilterCriteria.GT:
			currentFilter = PropertyFilter.gt(dbField.getName(), value);
			break;
		case FilterCriteria.GE:
			currentFilter = PropertyFilter.ge(dbField.getName(), value);
			break;
		default:
			currentFilter = null;
		}
		return currentFilter;
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
