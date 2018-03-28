package sfms.rest.models;

public class CrewMember {

	private String m_key;
	private String m_firstName;
	private String m_lastName;

	public CrewMember() {
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
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
