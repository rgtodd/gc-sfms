package sfms.rest.db.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.db.schemas.DbClusterField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;

public class RegionSet implements Iterable<Region> {

	private Set<Region> m_regions;

	private RegionSet(Set<Region> regions) {
		m_regions = regions;
	}

	public static RegionSet create(int minimum, int maximum, int delta) {

		Set<Region> regions = new HashSet<Region>();

		for (int x = minimum; x < maximum; x += delta) {
			for (int y = minimum; y < maximum; y += delta) {
				for (int z = minimum; z < maximum; z += delta) {
					Region region = new Region(x, y, z, x + delta, y + delta, z + delta);
					regions.add(region);
				}
			}
		}

		return new RegionSet(regions);
	}

	public static RegionSet loadClusters() {
		Set<Region> regions = new HashSet<Region>();

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(DbEntity.Cluster.getKind())
				.build();

		QueryResults<Entity> entities = datastore.run(query);
		while (entities.hasNext()) {
			Entity entity = entities.next();
			int minimumX = (int) entity.getLong(DbClusterField.MinimumX.getId());
			int minimumY = (int) entity.getLong(DbClusterField.MinimumY.getId());
			int minimumZ = (int) entity.getLong(DbClusterField.MinimumZ.getId());
			int maximumX = (int) entity.getLong(DbClusterField.MaximumX.getId());
			int maximumY = (int) entity.getLong(DbClusterField.MaximumY.getId());
			int maximumZ = (int) entity.getLong(DbClusterField.MaximumX.getId());

			Region region = new Region(minimumX, minimumY, minimumZ, maximumX, maximumY, maximumZ);
			regions.add(region);
		}

		return new RegionSet(regions);
	}

	public static RegionSet loadSectors() {
		Set<Region> regions = new HashSet<Region>();

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind(DbEntity.Sector.getKind())
				.build();

		QueryResults<Entity> entities = datastore.run(query);
		while (entities.hasNext()) {
			Entity entity = entities.next();
			int minimumX = (int) entity.getLong(DbSectorField.MinimumX.getId());
			int minimumY = (int) entity.getLong(DbSectorField.MinimumY.getId());
			int minimumZ = (int) entity.getLong(DbSectorField.MinimumZ.getId());
			int maximumX = (int) entity.getLong(DbSectorField.MaximumX.getId());
			int maximumY = (int) entity.getLong(DbSectorField.MaximumY.getId());
			int maximumZ = (int) entity.getLong(DbSectorField.MaximumX.getId());

			Region region = new Region(minimumX, minimumY, minimumZ, maximumX, maximumY, maximumZ);
			regions.add(region);
		}

		return new RegionSet(regions);
	}

	public void addAll(RegionSet regionSet) {
		m_regions.addAll(regionSet.m_regions);
	}

	public Region findClosestRegion(double x, double y, double z) {
		Coordinates coordinates = new Coordinates(x, y, z);

		Region currentRegion = null;
		double currentDistance = 0;
		for (Region region : this) {
			double distance = coordinates.getDistanceTo(region.getMidpoint());
			if (currentRegion == null || distance < currentDistance) {
				currentRegion = region;
				currentDistance = distance;
			}
		}

		return currentRegion;
	}

	@Override
	public Iterator<Region> iterator() {
		return m_regions.iterator();
	}

}
