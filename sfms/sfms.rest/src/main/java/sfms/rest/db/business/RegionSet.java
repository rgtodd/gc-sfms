package sfms.rest.db.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

	public void addAll(RegionSet regionSet) {
		m_regions.addAll(regionSet.m_regions);
	}

	@Override
	public Iterator<Region> iterator() {
		return m_regions.iterator();
	}

}
