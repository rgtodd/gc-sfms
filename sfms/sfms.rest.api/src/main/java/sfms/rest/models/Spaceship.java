package sfms.rest.models;

public class Spaceship {

	private Long m_id;
	private String m_name;

	public Long getId() {
		return m_id;
	}

	public void setId(Long id) {
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
