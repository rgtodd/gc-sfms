package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbSectorStarField implements DbFieldSchema {

	MinimumX("starkey", "Star Key", "Key of Star entity contained in this sector.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbSectorStarField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbSectorStarField parse(String id) {
		for (DbSectorStarField property : DbSectorStarField.values()) {
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
