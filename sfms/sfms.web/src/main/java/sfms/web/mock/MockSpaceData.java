package sfms.web.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import sfms.web.models.ajax.Sector;

public class MockSpaceData {

	private static final Random s_random = new Random();
	private static final AtomicInteger s_counter = new AtomicInteger();

	private List<Sector> m_sectors;
	private List<Point> m_stars;
	private List<Point> m_ships;

	public List<Sector> getSectors() {
		if (m_sectors == null) {
			List<Sector> sectors = new ArrayList<Sector>();
			for (int sx = 0; sx < 10; ++sx) {
				for (int sy = 0; sy < 10; ++sy) {
					for (int sz = 0; sz < 10; ++sz) {
						String key = Integer.toString(sx) + "," + Integer.toString(sy) + "," + Integer.toString(sz);
						int minimumX = -1000 + sx * 200;
						int minimumY = -1000 + sy * 200;
						int minimumZ = -1000 + sz * 200;
						int maximumX = minimumX + 200;
						int maximumY = minimumY + 200;
						int maximumZ = minimumZ + 200;

						Sector sector = new Sector();
						sector.setKey(key);
						sector.setSx(sx);
						sector.setSy(sy);
						sector.setSz(sz);
						sector.setMinimumX(minimumX);
						sector.setMinimumY(minimumY);
						sector.setMinimumZ(minimumZ);
						sector.setMaximumX(maximumX);
						sector.setMaximumY(maximumY);
						sector.setMaximumZ(maximumZ);
						sectors.add(sector);
					}
				}
			}
			m_sectors = sectors;
		}
		return m_sectors;

	}

	public List<Point> getStars() {
		if (m_stars == null) {
			m_stars = Point.createRandomPoints(150000);
		}
		return m_stars;
	}

	public List<Point> getShips() {
		if (m_ships == null) {
			m_ships = Point.createRandomPoints(1500);
		}
		return m_ships;
	}

	public static class Point {
		public String key;
		public double x;
		public double y;
		public double z;

		public static Point createRandomPoint() {
			Point point = new Point();
			point.key = "P" + Integer.toString(s_counter.incrementAndGet());
			point.x = -1000 + s_random.nextDouble() * 2000;
			point.y = -1000 + s_random.nextDouble() * 2000;
			point.z = -1000 + s_random.nextDouble() * 2000;
			return point;
		}

		public static List<Point> createRandomPoints(int count) {
			List<Point> points = new ArrayList<Point>();
			for (int idx = 0; idx < count; ++idx) {
				points.add(Point.createRandomPoint());
			}
			return points;
		}
	}

}
