package sfms.test.db;

public class DbSpaceship {

	private String m_key;
	private String m_name;

	public DbSpaceship(String key) {
		m_key = key;
	}

	public String getKey() {
		return m_key;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}
}
