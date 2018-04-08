package sfms.rest.db.business;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import sfms.rest.db.schemas.DbClusterField;
import sfms.rest.db.schemas.DbClusterSectorField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;

public class StarClusterGenerator {

	private final Logger logger = Logger.getLogger(StarClusterGenerator.class.getName());

	private static final int MIN_BOUNDS = -1000;
	private static final int MAX_BOUNDS = 1000;
	private static final int DELTA = 200;

	public void generate() {
		logger.info("Creating sector regions.");
		RegionSet sectorRegions = createSectorRegions();

		logger.info("Creating cluster regions.");
		RegionSet clusterRegions = createClusterRegions();

		logger.info("Generating sectors.");
		try {
			generateSectors(sectorRegions);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "generateSectors exception occurred.", e);
		}

		logger.info("Generating clusters.");
		try {
			generateClusters(clusterRegions);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "generateClusters exception occurred.", e);
		}

		logger.info("Generating cluster sectors.");
		try {
			generateClusterSectors(clusterRegions, sectorRegions);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "generateClusterSectors exception occurred.", e);
		}
	}

	private void generateSectors(RegionSet sectorRegions) throws Exception {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory sectorKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Sector.getKind());

		try (BatchPut batchPut = new BatchPut(datastore)) {
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
				batchPut.add(sector);
			}
		}
	}

	private void generateClusters(RegionSet clusterRegions) throws Exception {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory clusterKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Cluster.getKind());

		try (BatchPut batchPut = new BatchPut(datastore)) {
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
				batchPut.add(cluster);
			}
		}
	}

	private void generateClusterSectors(RegionSet clusterRegions, RegionSet sectorRegions) throws Exception {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory sectorKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Sector.getKind());
		KeyFactory clusterKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Cluster.getKind());
		KeyFactory clusterSectorKeyFactory = datastore.newKeyFactory().setKind(DbEntity.ClusterSector.getKind());

		try (BatchPut batchPut = new BatchPut(datastore)) {

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
						batchPut.add(clusterSector);
					}
				}
			}
		}
	}

	private RegionSet createSectorRegions() {
		return RegionSet.create(MIN_BOUNDS, MAX_BOUNDS, DELTA);
	}

	private RegionSet createClusterRegions() {
		RegionSet result = RegionSet.create(MIN_BOUNDS, MAX_BOUNDS, DELTA * 2);
		result.addAll(RegionSet.create(MIN_BOUNDS + DELTA, MAX_BOUNDS, DELTA * 2));
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
}
