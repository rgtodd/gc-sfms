package sfms.rest.controllers;

import java.util.ArrayList;
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

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.ClusterField;
import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueFactory;
import sfms.rest.db.schemas.DbClusterField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

/**
 * Controller for the Cluster REST service.
 * 
 * Provides basic CRUD operations for Cluster entities.
 *
 */
@RestController
@RequestMapping("/cluster")
public class ClusterRestController {

	private static final Map<String, DbFieldSchema> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, DbFieldSchema>();
		s_dbFieldMap.put(ClusterField.ClusterPartition.getName(), DbClusterField.ClusterPartition);
		s_dbFieldMap.put(ClusterField.ClusterX.getName(), DbClusterField.ClusterX);
		s_dbFieldMap.put(ClusterField.ClusterY.getName(), DbClusterField.ClusterY);
		s_dbFieldMap.put(ClusterField.ClusterZ.getName(), DbClusterField.ClusterZ);
		s_dbFieldMap.put(ClusterField.MinimumX.getName(), DbClusterField.MinimumX);
		s_dbFieldMap.put(ClusterField.MinimumY.getName(), DbClusterField.MinimumY);
		s_dbFieldMap.put(ClusterField.MinimumZ.getName(), DbClusterField.MinimumZ);
		s_dbFieldMap.put(ClusterField.MaximumX.getName(), DbClusterField.MaximumX);
		s_dbFieldMap.put(ClusterField.MaximumY.getName(), DbClusterField.MaximumY);
		s_dbFieldMap.put(ClusterField.MaximumZ.getName(), DbClusterField.MaximumZ);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;
	private static final String DETAIL_STAR = "star";

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{key}")
	public Cluster getLookupUncached(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, key);

		Entity dbCluster = datastore.get(dbClusterKey);

		Query<ProjectionEntity> dbStarQuery = Query.newProjectionEntityQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.addProjection(DbStarField.X.getName())
				.addProjection(DbStarField.Y.getName())
				.addProjection(DbStarField.Z.getName())
				.setFilter(PropertyFilter.eq(DbStarField.ClusterKey.getName(), dbClusterKey))
				.build();

		QueryResults<ProjectionEntity> dbStars = datastore.run(dbStarQuery);

		RestFactory factory = new RestFactory();
		Cluster cluster = factory.createCluster(dbCluster, factory.createStarsFromProjection(dbStars));

		return cluster;
	}

	@GetMapping(value = "")
	public SearchResult<Cluster> getSearch(
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

		RestQuery dbClusterQuery = RestQueryBuilder.newQueryBuilder(s_dbFieldMap)
				.setType(RestQueryBuilderType.ENTITY)
				.setKind(DbEntity.Cluster.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setQueryFilter(filter)
				.setStartCursor(bookmark)
				.build();

		RestQueryResults dbClusters = dbClusterQuery.run(datastore);

		List<Cluster> clusters;
		if (detail.isPresent() && detail.get().equals(DETAIL_STAR)) {
			clusters = getClustersWithDetail(datastore, dbClusters);
		} else {
			RestFactory factory = new RestFactory();
			clusters = factory.createClusters(dbClusters);
		}

		SearchResult<Cluster> result = new SearchResult<Cluster>();
		result.setEntities(clusters);
		result.setEndingBookmark(dbClusters.getCursorAfter().toUrlSafe());
		result.setEndOfResults(clusters.size() < limit);

		return result;
	}

	@PutMapping(value = "/{key}")
	public UpdateResult<String> putUpdate(@PathVariable String key, @RequestBody Cluster cluster) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, key);

		Entity dbCluster = createDbCluster(cluster, dbClusterKey);

		datastore.update(dbCluster);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(key);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> putCreate(@RequestBody Cluster cluster) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, cluster.getKey());

		Entity dbCluster = createDbCluster(cluster, dbClusterKey);

		datastore.put(dbCluster);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Cluster.createRestKey(dbClusterKey));

		return result;
	}

	@DeleteMapping(value = "/{key}")
	public DeleteResult<String> delete(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, key);

		datastore.delete(dbClusterKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.Cluster.createRestKey(dbClusterKey));

		return result;
	}

	private List<Cluster> getClustersWithDetail(Datastore datastore, RestQueryResults dbClusters) {

		RestFactory factory = new RestFactory();

		Long minimumX = null;
		Long maximumX = null;
		Long minimumY = null;
		Long maximumY = null;
		Long minimumZ = null;
		Long maximumZ = null;
		Map<String, Cluster> clustersByKey = new HashMap<String, Cluster>();
		while (dbClusters.hasNext()) {
			BaseEntity<Key> dbCluster = dbClusters.next();
			Cluster cluster = factory.createCluster(dbCluster, new ArrayList<Star>());
			if (minimumX == null || cluster.getMinimumX() < minimumX) {
				minimumX = cluster.getMinimumX();
			}
			if (maximumX == null || cluster.getMaximumX() > maximumX) {
				maximumX = cluster.getMaximumX();
			}
			if (minimumY == null || cluster.getMinimumY() < minimumY) {
				minimumY = cluster.getMinimumY();
			}
			if (maximumY == null || cluster.getMaximumY() > maximumY) {
				maximumY = cluster.getMaximumY();
			}
			if (minimumZ == null || cluster.getMinimumZ() < minimumZ) {
				minimumZ = cluster.getMinimumZ();
			}
			if (maximumZ == null || cluster.getMaximumZ() > maximumZ) {
				maximumZ = cluster.getMaximumZ();
			}
			clustersByKey.put(cluster.getKey(), cluster);
		}

		if (!clustersByKey.isEmpty()) {

			Query<ProjectionEntity> dbStarQuery = Query.newProjectionEntityQueryBuilder()
					.setKind(DbEntity.Star.getKind())
					.setFilter(CompositeFilter.and(
							PropertyFilter.ge(DbStarField.X.getName(), (double) minimumX),
							PropertyFilter.lt(DbStarField.X.getName(), (double) maximumX),
							PropertyFilter.ge(DbStarField.Y.getName(), (double) minimumY),
							PropertyFilter.lt(DbStarField.Y.getName(), (double) maximumY),
							PropertyFilter.ge(DbStarField.Z.getName(), (double) minimumZ),
							PropertyFilter.lt(DbStarField.Z.getName(), (double) maximumZ)))
					.addProjection(DbStarField.ClusterKey.getName())
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
					String starClusterKey = star.getClusterKey();
					if (starClusterKey != null) {
						Cluster cluster = clustersByKey.get(starClusterKey);
						if (cluster != null) {
							cluster.getStars().add(star);
						}
					}
				}
			}
		}

		List<Cluster> clusters = new ArrayList<Cluster>(clustersByKey.values());
		return clusters;
	}

	private Entity createDbCluster(Cluster cluster, Key dbClusterKey) {
		Entity dbCluster = Entity.newBuilder(dbClusterKey)
				.set(DbClusterField.ClusterPartition.getName(), DbValueFactory.asValue(cluster.getClusterPartition()))
				.set(DbClusterField.ClusterX.getName(), DbValueFactory.asValue(cluster.getClusterX()))
				.set(DbClusterField.ClusterY.getName(), DbValueFactory.asValue(cluster.getClusterY()))
				.set(DbClusterField.ClusterZ.getName(), DbValueFactory.asValue(cluster.getClusterZ()))
				.set(DbClusterField.MinimumX.getName(), DbValueFactory.asValue(cluster.getMinimumX()))
				.set(DbClusterField.MinimumY.getName(), DbValueFactory.asValue(cluster.getMinimumY()))
				.set(DbClusterField.MinimumZ.getName(), DbValueFactory.asValue(cluster.getMinimumZ()))
				.set(DbClusterField.MaximumX.getName(), DbValueFactory.asValue(cluster.getMaximumX()))
				.set(DbClusterField.MaximumY.getName(), DbValueFactory.asValue(cluster.getMaximumY()))
				.set(DbClusterField.MaximumZ.getName(), DbValueFactory.asValue(cluster.getMaximumZ()))
				.build();
		return dbCluster;
	}
}
