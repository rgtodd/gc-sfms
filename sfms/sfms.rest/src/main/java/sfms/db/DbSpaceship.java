package sfms.db;

public class DbSpaceship {

	private long m_id;
	private String m_name;

	public DbSpaceship(long id) {
		m_id = id;
	}

	public long getId() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}
}
