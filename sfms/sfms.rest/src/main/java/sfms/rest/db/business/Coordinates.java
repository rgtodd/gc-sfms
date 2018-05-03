package sfms.rest.db.business;

/**
 * Utility class used to represent a 3D vector.
 *
 */
public class Coordinates {

	private double m_x;
	private double m_y;
	private double m_z;

	public Coordinates(double x, double y, double z) {
		m_x = x;
		m_y = y;
		m_z = z;
	}

	public static double getDistance(Coordinates from, Coordinates to) {
		double dx = to.getX() - from.getX();
		double dy = to.getY() - from.getY();
		double dz = to.getZ() - from.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double getDistanceTo(Coordinates to) {
		return getDistance(this, to);
	}

	public double getX() {
		return m_x;
	}

	public double getY() {
		return m_y;
	}

	public double getZ() {
		return m_z;
	}

	@Override
	public String toString() {
		return "Coordinates [m_x=" + m_x + ", m_y=" + m_y + ", m_z=" + m_z + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(m_x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m_y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m_z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Coordinates))
			return false;
		Coordinates other = (Coordinates) obj;
		if (Double.doubleToLongBits(m_x) != Double.doubleToLongBits(other.m_x))
			return false;
		if (Double.doubleToLongBits(m_y) != Double.doubleToLongBits(other.m_y))
			return false;
		if (Double.doubleToLongBits(m_z) != Double.doubleToLongBits(other.m_z))
			return false;
		return true;
	}

}
