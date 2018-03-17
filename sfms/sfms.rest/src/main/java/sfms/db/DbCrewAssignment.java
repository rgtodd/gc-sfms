package sfms.db;

public class DbCrewAssignment {

	private long m_crewMemberId;
	private Long m_spaceshipId;
	private String m_department;

	public DbCrewAssignment(long crewMemberId) {
		m_crewMemberId = crewMemberId;
	}

	public long getCrewMemberId() {
		return m_crewMemberId;
	}

	public Long getSpaceshipId() {
		return m_spaceshipId;
	}

	public void setSpaceshipId(Long spaceshipId) {
		m_spaceshipId = spaceshipId;
	}

	public String getDepartment() {
		return m_department;
	}

	public void setDepartment(String department) {
		m_department = department;
	}

}
