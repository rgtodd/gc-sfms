package sfms.rest.db.business;

public class Region {

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