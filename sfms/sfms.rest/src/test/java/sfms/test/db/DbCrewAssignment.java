package sfms.test.db;

public class DbCrewAssignment {

	private String m_crewMemberKey;
	private String m_spaceshipKey;
	private String m_department;

	public DbCrewAssignment(String crewMemberKey) {
		m_crewMemberKey = crewMemberKey;
	}

	public String getCrewMemberKey() {
		return m_crewMemberKey;
	}

	public String getSpaceshipKey() {
		return m_spaceshipKey;
	}

	public void setSpaceshipKey(String spaceshipKey) {
		m_spaceshipKey = spaceshipKey;
	}

	public String getDepartment() {
		return m_department;
	}

	public void setDepartment(String department) {
		m_department = department;
	}

}
