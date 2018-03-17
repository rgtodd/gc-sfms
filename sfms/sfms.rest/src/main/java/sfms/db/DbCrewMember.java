package sfms.db;

public class DbCrewMember {

	public long m_id;
	public String m_firstName;
	public String m_lastName;

	public DbCrewMember(long id) {
		m_id = id;
	}

	public long getId() {
		return m_id;
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
