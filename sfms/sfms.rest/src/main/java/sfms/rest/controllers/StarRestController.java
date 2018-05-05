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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.StarField;
import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

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
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Star getLookup(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, id);

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

		Query<Entity> dbStarQuery = RestQueryBuilder.newRestQueryBuilder(s_dbFieldMap)
				.setKind(DbEntity.Star.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setQueryFilter(filter)
				.setStartCursor(bookmark)
				.build();

		QueryResults<Entity> dbStars = datastore.run(dbStarQuery);

		RestFactory factory = new RestFactory();
		List<Star> stars = factory.createStars(dbStars);

		SearchResult<Star> result = new SearchResult<Star>();
		result.setEntities(stars);
		result.setEndingBookmark(dbStars.getCursorAfter().toUrlSafe());
		result.setEndOfResults(stars.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> putUpdate(@PathVariable String id, @RequestBody Star star) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, id);

		Entity dbStar = Entity.newBuilder(dbStarKey)
				.set(DbStarField.CatalogId.getName(), star.getCatalogId())
				.set(DbStarField.ClusterKey.getName(),
						DbEntity.Cluster.createEntityKey(datastore, star.getClusterKey()))
				.set(DbStarField.SectorKey.getName(), DbEntity.Sector.createEntityKey(datastore, star.getProperName()))
				.set(DbStarField.HipparcosId.getName(), star.getHipparcosId())
				.set(DbStarField.HenryDraperId.getName(), star.getHenryDraperId())
				.set(DbStarField.HarvardRevisedId.getName(), star.getHarvardRevisedId())
				.set(DbStarField.GlieseId.getName(), star.getGlieseId())
				.set(DbStarField.BayerFlamsteedId.getName(), star.getBayerFlamsteedId())
				.set(DbStarField.ProperName.getName(), star.getProperName())
				.set(DbStarField.RightAscension.getName(), star.getRightAscension())
				.set(DbStarField.Declination.getName(), star.getDeclination())
				.set(DbStarField.Distance.getName(), star.getDistance())
				.set(DbStarField.ProperMotionRightAscension.getName(), star.getProperMotionRightAscension())
				.set(DbStarField.ProperMotionDeclination.getName(), star.getProperMotionDeclination())
				.set(DbStarField.RadialVelocity.getName(), star.getRadialVelocity())
				.set(DbStarField.Magnitude.getName(), star.getMagnitude())
				.set(DbStarField.AbsoluteMagnitude.getName(), star.getAbsoluteMagnitude())
				.set(DbStarField.Spectrum.getName(), star.getSpectrum())
				.set(DbStarField.ColorIndex.getName(), star.getColorIndex())
				.set(DbStarField.X.getName(), star.getX())
				.set(DbStarField.Y.getName(), star.getY())
				.set(DbStarField.Z.getName(), star.getZ())
				.set(DbStarField.VX.getName(), star.getVX())
				.set(DbStarField.VY.getName(), star.getVY())
				.set(DbStarField.VZ.getName(), star.getVZ())
				.set(DbStarField.RightAcensionRadians.getName(), star.getRightAcensionRadians())
				.set(DbStarField.DeclinationRadians.getName(), star.getDeclinationRadians())
				.set(DbStarField.ProperMotionRightAscensionRadians.getName(),
						star.getProperMotionRightAscensionRadians())
				.set(DbStarField.ProperMotionDeclinationRadians.getName(), star.getProperMotionDeclinationRadians())
				.set(DbStarField.BayerId.getName(), star.getBayerId())
				.set(DbStarField.Flamsteed.getName(), star.getFlamsteed())
				.set(DbStarField.Constellation.getName(), star.getConstellation())
				.set(DbStarField.CompanionStarId.getName(), star.getCompanionStarId())
				.set(DbStarField.PrimaryStarId.getName(), star.getPrimaryStarId())
				.set(DbStarField.MultipleStarId.getName(), star.getMultipleStarId())
				.set(DbStarField.Luminosity.getName(), star.getLuminosity())
				.set(DbStarField.VariableStarDesignation.getName(), star.getVariableStarDesignation())
				.set(DbStarField.VariableMinimum.getName(), star.getVariableMinimum())
				.set(DbStarField.VariableMaximum.getName(), star.getVariableMaximum())
				.build();

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

		Entity dbStar = Entity.newBuilder(dbStarKey)
				.set(DbStarField.CatalogId.getName(), star.getCatalogId())
				.set(DbStarField.ClusterKey.getName(),
						DbEntity.Cluster.createEntityKey(datastore, star.getClusterKey()))
				.set(DbStarField.SectorKey.getName(), DbEntity.Sector.createEntityKey(datastore, star.getProperName()))
				.set(DbStarField.HipparcosId.getName(), star.getHipparcosId())
				.set(DbStarField.HenryDraperId.getName(), star.getHenryDraperId())
				.set(DbStarField.HarvardRevisedId.getName(), star.getHarvardRevisedId())
				.set(DbStarField.GlieseId.getName(), star.getGlieseId())
				.set(DbStarField.BayerFlamsteedId.getName(), star.getBayerFlamsteedId())
				.set(DbStarField.ProperName.getName(), star.getProperName())
				.set(DbStarField.RightAscension.getName(), star.getRightAscension())
				.set(DbStarField.Declination.getName(), star.getDeclination())
				.set(DbStarField.Distance.getName(), star.getDistance())
				.set(DbStarField.ProperMotionRightAscension.getName(), star.getProperMotionRightAscension())
				.set(DbStarField.ProperMotionDeclination.getName(), star.getProperMotionDeclination())
				.set(DbStarField.RadialVelocity.getName(), star.getRadialVelocity())
				.set(DbStarField.Magnitude.getName(), star.getMagnitude())
				.set(DbStarField.AbsoluteMagnitude.getName(), star.getAbsoluteMagnitude())
				.set(DbStarField.Spectrum.getName(), star.getSpectrum())
				.set(DbStarField.ColorIndex.getName(), star.getColorIndex())
				.set(DbStarField.X.getName(), star.getX())
				.set(DbStarField.Y.getName(), star.getY())
				.set(DbStarField.Z.getName(), star.getZ())
				.set(DbStarField.VX.getName(), star.getVX())
				.set(DbStarField.VY.getName(), star.getVY())
				.set(DbStarField.VZ.getName(), star.getVZ())
				.set(DbStarField.RightAcensionRadians.getName(), star.getRightAcensionRadians())
				.set(DbStarField.DeclinationRadians.getName(), star.getDeclinationRadians())
				.set(DbStarField.ProperMotionRightAscensionRadians.getName(),
						star.getProperMotionRightAscensionRadians())
				.set(DbStarField.ProperMotionDeclinationRadians.getName(), star.getProperMotionDeclinationRadians())
				.set(DbStarField.BayerId.getName(), star.getBayerId())
				.set(DbStarField.Flamsteed.getName(), star.getFlamsteed())
				.set(DbStarField.Constellation.getName(), star.getConstellation())
				.set(DbStarField.CompanionStarId.getName(), star.getCompanionStarId())
				.set(DbStarField.PrimaryStarId.getName(), star.getPrimaryStarId())
				.set(DbStarField.MultipleStarId.getName(), star.getMultipleStarId())
				.set(DbStarField.Luminosity.getName(), star.getLuminosity())
				.set(DbStarField.VariableStarDesignation.getName(), star.getVariableStarDesignation())
				.set(DbStarField.VariableMinimum.getName(), star.getVariableMinimum())
				.set(DbStarField.VariableMaximum.getName(), star.getVariableMaximum())
				.build();

		datastore.put(dbStar);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Star.createRestKey(dbStarKey));

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<String> delete(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbStarKey = DbEntity.Star.createEntityKey(datastore, id);

		datastore.delete(dbStarKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.Star.createRestKey(dbStarKey));

		return result;
	}
}
