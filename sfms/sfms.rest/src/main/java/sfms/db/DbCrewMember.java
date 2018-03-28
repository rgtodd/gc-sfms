package sfms.db;

public class DbCrewMember {

	public String m_key;
	public String m_firstName;
	public String m_lastName;

	public DbCrewMember(String key) {
		m_key = key;
	}

	public String getKey() {
		return m_key;
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
