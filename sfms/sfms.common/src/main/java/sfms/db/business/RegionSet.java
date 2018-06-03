package sfms.db.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.db.CompositeKey;
import sfms.db.CompositeKeyBuilder;
import sfms.db.schemas.DbClusterField;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSectorField;

/**
 * A collection of contiguous {@link Region} objects over 3D space.
 * 
 */
public class RegionSet implements Iterable<Region> {

	private Set<Region> m_regions;

	private RegionSet(Set<Region> regions) {
		m_regions = regions;
	}

	public static RegionSet create(int minimum, int maximum, int delta, int regionPartition) {

		Set<Region> regions = new HashSet<Region>();

		int regionX = -1;
		for (int x = minimum; x < maximum; x += delta) {
			++regionX;

			int regionY = -1;
			for (int y = minimum; y < maximum; y += delta) {
				++regionY;

				int regionZ = -1;
				for (int z = minimum; z < maximum; z += delta) {
					++regionZ;

					CompositeKey key = CompositeKeyBuilder.create()
							.append(regionX, 2)
							.append(regionY, 2)
							.append(regionZ, 2)
							.append(regionPartition, 2)
							.build();

					Region region = new Region(key.toString(),
							regionPartition,
							regionX, regionY, regionZ,
							x, y, z,
							x + delta, y + delta, z + delta);
					regions.add(region);
				}
			}
		}

		return new RegionSet(regions);
	}

	public static RegionSet create(int minimum, int maximum, int delta) {

		Set<Region> regions = new HashSet<Region>();

		int regionX = -1;
		for (int x = minimum; x < maximum; x += delta) {
			++regionX;

			int regionY = -1;
			for (int y = minimum; y < maximum; y += delta) {
				++regionY;

				int regionZ = -1;
				for (int z = minimum; z < maximum; z += delta) {
					++regionZ;

					CompositeKey key = CompositeKeyBuilder.create()
							.append(regionX, 2)
							.append(regionY, 2)
							.append(regionZ, 2)
							.build();

					Region region = new Region(key.toString(),
							0,
							regionX, regionY, regionZ,
							x, y, z,
							x + delta, y + delta, z + delta);
					regions.add(region);
				}
			}
		}

		return new RegionSet(regions);
	}

	public static RegionSet loadClusters() {
		Set<Region> regions = new HashSet<Region>();

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = Query.newEntityQueryBuilder().setKind(DbEntity.Cluster.getKind()).build();

		QueryResults<Entity> entities = datastore.run(query);
		while (entities.hasNext()) {
			Entity entity = entities.next();
			int clusterPartition = (int) entity.getLong(DbClusterField.ClusterPartition.getName());
			int clusterX = (int) entity.getLong(DbClusterField.ClusterX.getName());
			int clusterY = (int) entity.getLong(DbClusterField.ClusterY.getName());
			int clusterZ = (int) entity.getLong(DbClusterField.ClusterZ.getName());
			int minimumX = (int) entity.getLong(DbClusterField.MinimumX.getName());
			int minimumY = (int) entity.getLong(DbClusterField.MinimumY.getName());
			int minimumZ = (int) entity.getLong(DbClusterField.MinimumZ.getName());
			int maximumX = (int) entity.getLong(DbClusterField.MaximumX.getName());
			int maximumY = (int) entity.getLong(DbClusterField.MaximumY.getName());
			int maximumZ = (int) entity.getLong(DbClusterField.MaximumZ.getName());

			Region region = new Region(entity.getKey().getName(), clusterPartition, clusterX, clusterY, clusterZ,
					minimumX, minimumY, minimumZ, maximumX, maximumY, maximumZ);
			regions.add(region);
		}

		return new RegionSet(regions);
	}

	public static RegionSet loadSectors() {
		Set<Region> regions = new HashSet<Region>();

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = Query.newEntityQueryBuilder().setKind(DbEntity.Sector.getKind()).build();

		QueryResults<Entity> entities = datastore.run(query);
		while (entities.hasNext()) {
			Entity entity = entities.next();
			int sectorX = (int) entity.getLong(DbSectorField.SectorX.getName());
			int sectorY = (int) entity.getLong(DbSectorField.SectorY.getName());
			int sectorZ = (int) entity.getLong(DbSectorField.SectorZ.getName());
			int minimumX = (int) entity.getLong(DbSectorField.MinimumX.getName());
			int minimumY = (int) entity.getLong(DbSectorField.MinimumY.getName());
			int minimumZ = (int) entity.getLong(DbSectorField.MinimumZ.getName());
			int maximumX = (int) entity.getLong(DbSectorField.MaximumX.getName());
			int maximumY = (int) entity.getLong(DbSectorField.MaximumY.getName());
			int maximumZ = (int) entity.getLong(DbSectorField.MaximumZ.getName());

			Region region = new Region(entity.getKey().getName(), 0, sectorX, sectorY, sectorZ, minimumX, minimumY,
					minimumZ, maximumX, maximumY, maximumZ);
			regions.add(region);
		}

		return new RegionSet(regions);
	}

	public void addAll(RegionSet regionSet) {
		m_regions.addAll(regionSet.m_regions);
	}

	public Region findContainingRegion(double x, double y, double z) {
		Coordinates coordinates = new Coordinates(x, y, z);

		for (Region region : this) {
			if (region.contains(coordinates)) {
				return region;
			}
		}

		return null;
	}

	public Region findClosestRegion(double x, double y, double z) {
		Coordinates coordinates = new Coordinates(x, y, z);

		Region currentRegion = null;
		double currentDistance = 0;
		for (Region region : this) {
			if (region.contains(coordinates)) {
				double distance = coordinates.getDistanceTo(region.getMidpoint());
				if (currentRegion == null || distance < currentDistance) {
					currentRegion = region;
					currentDistance = distance;
				}
			}
		}

		return currentRegion;
	}

	@Override
	public Iterator<Region> iterator() {
		return m_regions.iterator();
	}

}
