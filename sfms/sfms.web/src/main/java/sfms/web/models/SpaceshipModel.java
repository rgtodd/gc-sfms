package sfms.web.models;

public class SpaceshipModel {

	private String m_key;
	private String m_name;

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
}
