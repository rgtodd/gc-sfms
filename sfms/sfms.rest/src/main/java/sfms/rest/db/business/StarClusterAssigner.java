package sfms.rest.db.business;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

public class StarClusterAssigner {

	private Datastore m_datastore;
	private KeyFactory m_clusterKeyFactory;
	private KeyFactory m_sectorKeyFactory;
	private RegionSet m_clusters;
	private RegionSet m_sectors;

	public void initialize() {
		m_datastore = DatastoreOptions.getDefaultInstance().getService();
		m_clusterKeyFactory = m_datastore.newKeyFactory().setKind(DbEntity.Cluster.getKind());
		m_sectorKeyFactory = m_datastore.newKeyFactory().setKind(DbEntity.Sector.getKind());
		m_clusters = RegionSet.loadClusters();
		m_sectors = RegionSet.loadSectors();
	}

	public void process() {

		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.build();

		QueryResults<Entity> entities = m_datastore.run(query);
		while (entities.hasNext()) {
			Entity entity = entities.next();
			processEntity(entity);
		}
	}

	private void processEntity(Entity star) {

		int x = (int) star.getLong(DbStarField.X.getId());
		int y = (int) star.getLong(DbStarField.Y.getId());
		int z = (int) star.getLong(DbStarField.Z.getId());

		Region cluster = m_clusters.findClosestRegion(x, y, z);
		Key clusterKey = m_clusterKeyFactory.newKey(cluster.getKey());

		Region sector = m_sectors.findClosestRegion(x, y, z);
		Key sectorKey = m_sectorKeyFactory.newKey(sector.getKey());

		Entity updatedStar = Entity.newBuilder(star)
				.set(DbStarField.ClusterKey.getId(), clusterKey)
				.set(DbStarField.SectorKey.getId(), sectorKey)
				.build();

		m_datastore.put(updatedStar);
	}

}
