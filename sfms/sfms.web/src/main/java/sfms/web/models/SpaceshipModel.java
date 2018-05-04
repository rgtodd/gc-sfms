package sfms.web.models;

public class SpaceshipModel {

	private String m_key;
	private String m_name;
	private Long m_x;
	private Long m_y;
	private Long m_z;
	private String m_starKey;

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	@Override
	public String toString() {
		return "Key = " + getKey() + ", Name = " + getName();
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public Long getX() {
		return m_x;
	}

	public void setX(Long x) {
		m_x = x;
	}

	public Long getY() {
		return m_y;
	}

	public void setY(Long y) {
		m_y = y;
	}

	public Long getZ() {
		return m_z;
	}

	public void setZ(Long z) {
		m_z = z;
	}

	public String getStarKey() {
		return m_starKey;
	}

	public void setStarKey(String starKey) {
		m_starKey = starKey;
	}

	public String getXYZ() {
		return ModuleUtility.formatCoordinates(m_x, m_y, m_z);
	}
}
