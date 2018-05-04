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
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Cluster;
import sfms.rest.api.schemas.ClusterField;
import sfms.rest.db.DbFieldSchema;
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
		s_dbFieldMap.put(ClusterField.MinimumX.getName(), DbClusterField.MinimumX);
		s_dbFieldMap.put(ClusterField.MinimumY.getName(), DbClusterField.MinimumY);
		s_dbFieldMap.put(ClusterField.MinimumZ.getName(), DbClusterField.MinimumZ);
		s_dbFieldMap.put(ClusterField.MaximumX.getName(), DbClusterField.MaximumX);
		s_dbFieldMap.put(ClusterField.MaximumY.getName(), DbClusterField.MaximumY);
		s_dbFieldMap.put(ClusterField.MaximumZ.getName(), DbClusterField.MaximumZ);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{id}")
	public Cluster getLookup(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, id);
		Entity dbCluster = datastore.get(dbClusterKey);

		Query<ProjectionEntity> dbStarQuery = Query.newProjectionEntityQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.addProjection(DbStarField.X.getName())
				.addProjection(DbStarField.Y.getName())
				.addProjection(DbStarField.Z.getName())
				.setFilter(PropertyFilter.eq(DbStarField.ClusterKey.getName(), dbClusterKey)).build();

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

		Query<Entity> dbClusterQuery = RestQueryBuilder.newRestQueryBuilder(s_dbFieldMap)
				.setKind(DbEntity.Cluster.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setQueryFilter(filter)
				.setStartCursor(bookmark)
				.build();

		QueryResults<Entity> dbClusters = datastore.run(dbClusterQuery);

		RestFactory factory = new RestFactory();
		List<Cluster> clusters = factory.createClusters(dbClusters);

		SearchResult<Cluster> result = new SearchResult<Cluster>();
		result.setEntities(clusters);
		result.setEndingBookmark(dbClusters.getCursorAfter().toUrlSafe());
		result.setEndOfResults(clusters.size() < limit);

		return result;
	}

	@PutMapping(value = "/{id}")
	public UpdateResult<String> putUpdate(@PathVariable String id, @RequestBody Cluster cluster) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, id);

		Entity dbCluster = Entity.newBuilder(dbClusterKey)
				.set(DbClusterField.MinimumX.getName(), cluster.getMinimumX())
				.set(DbClusterField.MinimumY.getName(), cluster.getMinimumY())
				.set(DbClusterField.MinimumZ.getName(), cluster.getMinimumZ())
				.set(DbClusterField.MaximumX.getName(), cluster.getMaximumX())
				.set(DbClusterField.MaximumY.getName(), cluster.getMaximumY())
				.set(DbClusterField.MaximumZ.getName(), cluster.getMaximumZ())
				.build();

		datastore.update(dbCluster);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(id);

		return result;
	}

	@PutMapping(value = "")
	public CreateResult<String> putCreate(@RequestBody Cluster cluster) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, cluster.getKey());

		Entity dbCluster = Entity.newBuilder(dbClusterKey)
				.set(DbClusterField.MinimumX.getName(), cluster.getMinimumX())
				.set(DbClusterField.MinimumY.getName(), cluster.getMinimumY())
				.set(DbClusterField.MinimumZ.getName(), cluster.getMinimumZ())
				.set(DbClusterField.MaximumX.getName(), cluster.getMaximumX())
				.set(DbClusterField.MaximumY.getName(), cluster.getMaximumY())
				.set(DbClusterField.MaximumZ.getName(), cluster.getMaximumZ())
				.build();

		datastore.put(dbCluster);

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Cluster.createRestKey(dbClusterKey));

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<String> delete(@PathVariable String id) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbClusterKey = DbEntity.Cluster.createEntityKey(datastore, id);

		datastore.delete(dbClusterKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.Cluster.createRestKey(dbClusterKey));

		return result;
	}
}
