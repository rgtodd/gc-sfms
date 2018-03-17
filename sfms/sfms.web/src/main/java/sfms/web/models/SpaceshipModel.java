package sfms.web.models;

public class SpaceshipModel {

	private String m_id;
	private String m_name;

	public String getId() {
		return m_id;
	}

	public void setId(String id) {
		m_id = id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	@Override
	public String toString() {
		return "Id = " + getId() + ", Name = " + getName();
	}
}
