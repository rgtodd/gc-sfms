package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbClusterSectorField implements DbFieldSchema {

	MinimumX("sectorkey", "Sector Key", "Key of Sector entity contained in this cluster.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbClusterSectorField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbClusterSectorField parse(String id) {
		for (DbClusterSectorField property : DbClusterSectorField.values()) {
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
