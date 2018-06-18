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
import sfms.db.schemas.DbClusterField;
import sfms.db.schemas.DbEntity;
import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Cluster;
import sfms.rest.api.schemas.ClusterField;
import sfms.rest.db.RestQuery;
import sfms.rest.db.RestQueryBuilder;
import sfms.rest.db.RestQueryResults;

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
	private static final int MAX_PAGE_SIZE = 100000;

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

		RestFactory factory = new RestFactory();
		Cluster cluster = factory.createCluster(dbCluster);

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

		RestQuery dbClusterQuery = RestQueryBuilder.newEntityQueryBuilder(s_dbFieldMap)
				.setKind(DbEntity.Cluster.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setFilterCriteria(datastore, filter)
				.setStartCursor(bookmark)
				.build();

		RestQueryResults dbClusters = dbClusterQuery.run(datastore);

		RestFactory factory = new RestFactory();
		List<Cluster> clusters = factory.createClusters(dbClusters);

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
