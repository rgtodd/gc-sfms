package sfms.web.models;

public class StarModel {

	private String m_key;
	private String m_starId;
	private String m_properName;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public String getStarId() {
		return m_starId;
	}

	public void setStarId(String starId) {
		m_starId = starId;
	}

	public String getProperName() {
		return m_properName;
	}

	public void setProperName(String name) {
		m_properName = name;
	}

	@Override
	public String toString() {
		return "Key = " + getKey() +
				", StarId = " + getStarId() +
				", Proper Name = " + getProperName();
	}
}
