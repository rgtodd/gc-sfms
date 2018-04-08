package sfms.rest.db.business;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import sfms.rest.controllers.UtilityRestController;
import sfms.rest.db.schemas.DbClusterField;
import sfms.rest.db.schemas.DbClusterSectorField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;

public class StarClusterGenerator {

	private final Logger logger = Logger.getLogger(StarClusterGenerator.class.getName());

	private static final int MIN_BOUNDS = -1000;
	private static final int MAX_BOUNDS = 1000;
	private static final int DELTA = 200;
	private static final int BATCH_SIZE = 100;
	private static final Entity[] FULL_ENTITY_ARRAY_PROTOTYPE = new Entity[0];

	public void generate() {
		List<Region> sectorRegions = createSectorRegions();
		List<Region> clusterRegions = createClusterRegions();

		generateSectors(sectorRegions);
		generateClusters(clusterRegions);
		generateClusterSectors(clusterRegions, sectorRegions);
	}

	private void generateSectors(List<Region> sectorRegions) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory sectorKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Sector.getKind());

		List<Entity> sectors = new ArrayList<Entity>();

		for (Region sectorRegion : sectorRegions) {
			Key sectorKey = sectorKeyFactory.newKey(sectorRegion.getKey());
			Entity sector = Entity.newBuilder(sectorKey)
					.set(DbSectorField.MinimumX.getId(), sectorRegion.getMinimumX())
					.set(DbSectorField.MinimumY.getId(), sectorRegion.getMinimumY())
					.set(DbSectorField.MinimumZ.getId(), sectorRegion.getMinimumZ())
					.set(DbSectorField.MaximumX.getId(), sectorRegion.getMaximumX())
					.set(DbSectorField.MaximumY.getId(), sectorRegion.getMaximumY())
					.set(DbSectorField.MaximumZ.getId(), sectorRegion.getMaximumZ())
					.build();
			sectors.add(sector);

			if (sectors.size() > BATCH_SIZE) {
				writeEntites(datastore, sectors);
			}

		}

		writeEntites(datastore, sectors);
	}

	private void generateClusters(List<Region> clusterRegions) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory clusterKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Cluster.getKind());

		List<Entity> clusters = new ArrayList<Entity>();

		for (Region clusterRegion : clusterRegions) {
			Key clusterKey = clusterKeyFactory.newKey(clusterRegion.getKey());
			Entity cluster = Entity.newBuilder(clusterKey)
					.set(DbClusterField.MinimumX.getId(), clusterRegion.getMinimumX())
					.set(DbClusterField.MinimumY.getId(), clusterRegion.getMinimumY())
					.set(DbClusterField.MinimumZ.getId(), clusterRegion.getMinimumZ())
					.set(DbClusterField.MaximumX.getId(), clusterRegion.getMaximumX())
					.set(DbClusterField.MaximumY.getId(), clusterRegion.getMaximumY())
					.set(DbClusterField.MaximumZ.getId(), clusterRegion.getMaximumZ())
					.build();
			clusters.add(cluster);

			if (clusters.size() > BATCH_SIZE) {
				writeEntites(datastore, clusters);
			}
		}

		writeEntites(datastore, clusters);
	}

	private void generateClusterSectors(List<Region> clusterRegions, List<Region> sectorRegions) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory sectorKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Sector.getKind());
		KeyFactory clusterKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Cluster.getKind());
		KeyFactory clusterSectorKeyFactory = datastore.newKeyFactory().setKind(DbEntity.ClusterSector.getKind());

		List<Entity> clusterSectors = new ArrayList<Entity>();

		for (Region clusterRegion : clusterRegions) {
			Key clusterKey = clusterKeyFactory.newKey(clusterRegion.getKey());
			int idx = 0;

			for (Region sectorRegion : sectorRegions) {
				if (clusterRegion.contains(sectorRegion)) {
					Key sectorKey = sectorKeyFactory.newKey(sectorRegion.getKey());
					idx += 1;

					Key clusterSectorKey = clusterSectorKeyFactory.newKey(idx + "-" + clusterRegion.getKey());
					Entity clusterSector = Entity.newBuilder(clusterSectorKey)
							.set(DbClusterSectorField.ClusterKey.getId(), clusterKey)
							.set(DbClusterSectorField.SectorKey.getId(), sectorKey)
							.build();
					clusterSectors.add(clusterSector);

					if (clusterSectors.size() > BATCH_SIZE) {
						writeEntites(datastore, clusterSectors);
					}
				}
			}
		}

		writeEntites(datastore, clusterSectors);
	}

	private void writeEntites(Datastore datastore, List<Entity> entities) {
		datastore.put(entities.toArray(FULL_ENTITY_ARRAY_PROTOTYPE));
		entities.clear();
	}

	private List<Region> createSectorRegions() {
		return createRegions(MIN_BOUNDS, MAX_BOUNDS, DELTA);
	}

	private List<Region> createClusterRegions() {
		List<Region> result = new ArrayList<Region>();
		result.addAll(createRegions(MIN_BOUNDS, MAX_BOUNDS, DELTA * 2));
		result.addAll(createRegions(MIN_BOUNDS + DELTA, MAX_BOUNDS, DELTA * 2));
		return result;
	}

	private List<Region> createRegions(int minimum, int maximum, int delta) {
		List<Region> result = new ArrayList<Region>();

		for (int x = minimum; x < maximum; x += delta) {
			for (int y = minimum; y < maximum; y += delta) {
				for (int z = minimum; z < maximum; z += delta) {
					Region region = new Region(x, y, z, x + delta, y + delta, z + delta);
					result.add(region);
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unused")
	private Entity lookup(Iterable<Entity> entities, String key) throws Exception {
		for (Entity entity : entities) {
			if (entity.getKey().getName().equals(key)) {
				return entity;
			}
		}
		throw new Exception("Key" + String.valueOf(key) + " not found.");
	}

	private static class Region {

		private int m_minimumX;
		private int m_minimumY;
		private int m_minimumZ;
		private int m_maximumX;
		private int m_maximumY;
		private int m_maximumZ;
		private String m_key;

		public Region(int minimumX, int minimumY, int minimumZ, int maximumX, int maximumY, int maximumZ) {
			m_minimumX = minimumX;
			m_minimumY = minimumY;
			m_minimumZ = minimumZ;
			m_maximumX = maximumX;
			m_maximumY = maximumY;
			m_maximumZ = maximumZ;

			String key = Integer.toString(m_minimumX) + "," + Integer.toString(m_minimumY) + ","
					+ Integer.toString(m_minimumZ);
			m_key = key + "-" + Integer.toString(key.hashCode());
		}

		public int getMinimumX() {
			return m_minimumX;
		}

		public int getMinimumY() {
			return m_minimumY;
		}

		public int getMinimumZ() {
			return m_minimumZ;
		}

		public int getMaximumX() {
			return m_maximumX;
		}

		public int getMaximumY() {
			return m_maximumY;
		}

		public int getMaximumZ() {
			return m_maximumZ;
		}

		public String getKey() {
			return m_key;
		}

		public boolean contains(Region subregion) {
			return getMinimumX() <= subregion.getMinimumX() &&
					getMinimumY() <= subregion.getMinimumY() &&
					getMinimumZ() <= subregion.getMinimumZ() &&
					subregion.getMaximumX() <= getMaximumX() &&
					subregion.getMaximumY() <= getMaximumY() &&
					subregion.getMaximumZ() <= getMaximumZ();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + m_maximumX;
			result = prime * result + m_maximumY;
			result = prime * result + m_maximumZ;
			result = prime * result + m_minimumX;
			result = prime * result + m_minimumY;
			result = prime * result + m_minimumZ;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Region))
				return false;
			Region other = (Region) obj;
			if (m_maximumX != other.m_maximumX)
				return false;
			if (m_maximumY != other.m_maximumY)
				return false;
			if (m_maximumZ != other.m_maximumZ)
				return false;
			if (m_minimumX != other.m_minimumX)
				return false;
			if (m_minimumY != other.m_minimumY)
				return false;
			if (m_minimumZ != other.m_minimumZ)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Region [m_key=" + m_key + ", m_minimumX=" + m_minimumX + ", m_minimumY=" + m_minimumY
					+ ", m_minimumZ=" + m_minimumZ + ", m_maximumX=" + m_maximumX + ", m_maximumY=" + m_maximumY
					+ ", m_maximumZ=" + m_maximumZ + "]";
		}
	}
}
