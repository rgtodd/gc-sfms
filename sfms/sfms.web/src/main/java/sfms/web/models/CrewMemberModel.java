package sfms.web.models;

public class CrewMemberModel {

	private String m_id;
	private String m_firstName;
	private String m_lastName;

	public CrewMemberModel() {
	}

	public String getId() {
		return m_id;
	}

	public void setId(String id) {
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

	@Override
	public String toString() {
		return "Id = " + getId() + ", First Name = " + getFirstName() + ", Last Name = " + getLastName();
	}
}
