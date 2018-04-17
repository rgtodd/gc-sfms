package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbSpaceshipField implements DbFieldSchema {

	Name("n", "Name", "Name of spaceship."),
	X("x", "X", "X coordinate of spaceship."),
	Y("y", "Y", "Y coordinate of spaceship."),
	Z("z", "Z", "Z coordinate of spaceship."),
	StarKey("str_k", "Star Key", "Key of Star entity locating the spaceship.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbSpaceshipField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbSpaceshipField parse(String id) {
		for (DbSpaceshipField property : DbSpaceshipField.values()) {
			if (property.getName().equals(id)) {
				return property;
			}
		}

		return null;
	}

	public String getName() {
		return m_id;
	}

	public String getTitle() {
		return m_name;
	}

	public String getDescription() {
		return m_description;
	}
}
