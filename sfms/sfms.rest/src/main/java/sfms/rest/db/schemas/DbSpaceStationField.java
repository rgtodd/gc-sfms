package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbSpaceStationField implements DbFieldSchema {

	Name("n", "Name", "Name of space station."),
	StarKey("str_k", "Star Key", "Key of Star entity locating the space station.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbSpaceStationField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbSpaceStationField parse(String id) {
		for (DbSpaceStationField property : DbSpaceStationField.values()) {
			if (property.getId().equals(id)) {
				return property;
			}
		}

		return null;
	}

	public String getId() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public String getDescription() {
		return m_description;
	}
}
