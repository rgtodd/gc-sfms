package sfms.rest.models;

public class CrewMember {

	private Long m_id;
	private String m_firstName;
	private String m_lastName;

	public CrewMember() {
	}

	public Long getId() {
		return m_id;
	}

	public void setId(Long id) {
		m_id = id;
	}

	public String getFirstName() {
		return m_firstName;
	}

	public void setFirstName(String firstName) {
		m_firstName = firstName;
	}

	public String getLastName() {
		return m_lastName;
	}

	public void setLastName(String lastName) {
		m_lastName = lastName;
	}
}
