package sfms.rest.db.schemas;

public enum DbSpaceshipField {

	Name("n");

	private String m_name;

	private DbSpaceshipField(String name) {
		m_name = name;
	}

	public static DbSpaceshipField parse(String name) {
		for (DbSpaceshipField property : DbSpaceshipField.values()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		return null;
	}

	public String getName() {
		return m_name;
	}

}
