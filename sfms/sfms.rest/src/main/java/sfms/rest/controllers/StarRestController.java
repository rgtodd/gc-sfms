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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.DbFieldSchema;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbStarField;
import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.FilterCriteria;
import sfms.rest.api.RestDetail;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SelectionCriteria;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.StarField;
import sfms.rest.db.RestQuery;
import sfms.rest.db.RestQueryBuilder;
import sfms.rest.db.RestQueryResults;

/**
 * Controller for the Star REST service.
 * 
 * Provides basic CRUD operations for Star entities.
 * 
 */
@RestController
@RequestMapping("/star")
public class StarRestController {

	private static final Map<String, DbFieldSchema> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, DbFieldSchema>();
		s_dbFieldMap.put(StarField.X.getName(), DbStarField.X);
		s_dbFieldMap.put(StarField.Y.getName(), DbStarField.Y);
		s_dbFieldMap.put(StarField.Z.getName(), DbStarField.Z);
		s_dbFieldMap.put(StarField.CatalogId.getName(), DbStarField.CatalogId);
		s_dbFieldMap.put(StarField.ProperName.getName(), DbStarField.ProperName);
		s_dbFieldMap.put(StarField.ClusterKey.getName(), DbStarField.ClusterKey);
		s_dbFieldMap.put(StarField.SectorKey.getName(), DbStarField.SectorKey);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100000;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{key}")
	public Star getLookup(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, key);

		Entity dbStar = datastore.get(dbStarKey);

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(dbStar);

		return star;
	}

	@GetMapping(value = "")
	public SearchResult<Star> getSearch(
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

		FilterCriteria filterCriteria = filter.isPresent() ? FilterCriteria.parse(filter.get()) : null;

		SelectionCriteria selectionCriteria;
		if (detail.isPresent() && detail.get().equals(RestDetail.MINIMAL)) {
			selectionCriteria = SelectionCriteria.newBuilder(filterCriteria)
					.select(StarField.X.getName())
					.select(StarField.Y.getName())
					.select(StarField.Z.getName())
					.select(StarField.ClusterKey.getName())
					.select(StarField.SectorKey.getName())
					.build();
		} else {
			selectionCriteria = null;
		}

		RestQuery dbStarQuery = RestQueryBuilder.newQueryBuilder(s_dbFieldMap, selectionCriteria)
				.setKind(DbEntity.Star.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setFilterCriteria(datastore, filterCriteria)
				.setStartCursor(bookmark)
				.build();

		RestQueryResults dbStars = dbStarQuery.run(datastore);

		RestFactory factory = new RestFactory();
		List<Star> stars = factory.createStars(dbStars);

		SearchResult<Star> result = new SearchResult<Star>();
		result.setEntities(stars);
		result.setEndingBookmark(dbStars.getCursorAfter().toUrlSafe());
		result.setEndOfResults(stars.size() < limit);

		return result;
	}

	@PutMapping(value = "/{key}")
	public UpdateResult<String> putUpdate(@PathVariable String key, @RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, key);

		Entity dbStar = createDbStar(star, datastore, dbStarKey);

		datastore.update(dbStar);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(DbEntity.Star.createRestKey(dbStarKey));

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> putCreate(@RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, star.getKey());

		Entity dbStar = createDbStar(star, datastore, dbStarKey);

		datastore.put(dbStar);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Star.createRestKey(dbStarKey));

		return result;
	}

	@DeleteMapping(value = "/{key}")
	public DeleteResult<String> delete(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, key);

		datastore.delete(dbStarKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.Star.createRestKey(dbStarKey));

		return result;
	}

	private Entity createDbStar(Star star, Datastore datastore, Key dbStarKey) {
		Entity dbStar = Entity.newBuilder(dbStarKey)
				.set(DbStarField.CatalogId.getName(), DbValueFactory.asValue(star.getCatalogId()))
				.set(DbStarField.ClusterKey.getName(), DbValueFactory.asValue(
						DbEntity.Cluster.createEntityKey(datastore, star.getClusterKey())))
				.set(DbStarField.SectorKey.getName(),
						DbValueFactory.asValue(DbEntity.Sector.createEntityKey(datastore, star.getProperName())))
				.set(DbStarField.HipparcosId.getName(), DbValueFactory.asValue(star.getHipparcosId()))
				.set(DbStarField.HenryDraperId.getName(), DbValueFactory.asValue(star.getHenryDraperId()))
				.set(DbStarField.HarvardRevisedId.getName(), DbValueFactory.asValue(star.getHarvardRevisedId()))
				.set(DbStarField.GlieseId.getName(), DbValueFactory.asValue(star.getGlieseId()))
				.set(DbStarField.BayerFlamsteedId.getName(), DbValueFactory.asValue(star.getBayerFlamsteedId()))
				.set(DbStarField.ProperName.getName(), DbValueFactory.asValue(star.getProperName()))
				.set(DbStarField.RightAscension.getName(), DbValueFactory.asValue(star.getRightAscension()))
				.set(DbStarField.Declination.getName(), DbValueFactory.asValue(star.getDeclination()))
				.set(DbStarField.Distance.getName(), DbValueFactory.asValue(star.getDistance()))
				.set(DbStarField.ProperMotionRightAscension.getName(),
						DbValueFactory.asValue(star.getProperMotionRightAscension()))
				.set(DbStarField.ProperMotionDeclination.getName(),
						DbValueFactory.asValue(star.getProperMotionDeclination()))
				.set(DbStarField.RadialVelocity.getName(), DbValueFactory.asValue(star.getRadialVelocity()))
				.set(DbStarField.Magnitude.getName(), DbValueFactory.asValue(star.getMagnitude()))
				.set(DbStarField.AbsoluteMagnitude.getName(), DbValueFactory.asValue(star.getAbsoluteMagnitude()))
				.set(DbStarField.Spectrum.getName(), DbValueFactory.asValue(star.getSpectrum()))
				.set(DbStarField.ColorIndex.getName(), DbValueFactory.asValue(star.getColorIndex()))
				.set(DbStarField.X.getName(), DbValueFactory.asValue(star.getX()))
				.set(DbStarField.Y.getName(), DbValueFactory.asValue(star.getY()))
				.set(DbStarField.Z.getName(), DbValueFactory.asValue(star.getZ()))
				.set(DbStarField.VX.getName(), DbValueFactory.asValue(star.getVX()))
				.set(DbStarField.VY.getName(), DbValueFactory.asValue(star.getVY()))
				.set(DbStarField.VZ.getName(), DbValueFactory.asValue(star.getVZ()))
				.set(DbStarField.RightAcensionRadians.getName(), DbValueFactory.asValue(star.getRightAcensionRadians()))
				.set(DbStarField.DeclinationRadians.getName(), DbValueFactory.asValue(star.getDeclinationRadians()))
				.set(DbStarField.ProperMotionRightAscensionRadians.getName(),
						DbValueFactory.asValue(star.getProperMotionRightAscensionRadians()))
				.set(DbStarField.ProperMotionDeclinationRadians.getName(),
						DbValueFactory.asValue(star.getProperMotionDeclinationRadians()))
				.set(DbStarField.BayerId.getName(), DbValueFactory.asValue(star.getBayerId()))
				.set(DbStarField.Flamsteed.getName(), DbValueFactory.asValue(star.getFlamsteed()))
				.set(DbStarField.Constellation.getName(), DbValueFactory.asValue(star.getConstellation()))
				.set(DbStarField.CompanionStarId.getName(), DbValueFactory.asValue(star.getCompanionStarId()))
				.set(DbStarField.PrimaryStarId.getName(), DbValueFactory.asValue(star.getPrimaryStarId()))
				.set(DbStarField.MultipleStarId.getName(), DbValueFactory.asValue(star.getMultipleStarId()))
				.set(DbStarField.Luminosity.getName(), DbValueFactory.asValue(star.getLuminosity()))
				.set(DbStarField.VariableStarDesignation.getName(),
						DbValueFactory.asValue(star.getVariableStarDesignation()))
				.set(DbStarField.VariableMinimum.getName(), DbValueFactory.asValue(star.getVariableMinimum()))
				.set(DbStarField.VariableMaximum.getName(), DbValueFactory.asValue(star.getVariableMaximum()))
				.build();
		return dbStar;
	}
}
