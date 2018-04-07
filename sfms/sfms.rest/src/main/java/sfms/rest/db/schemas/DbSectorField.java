package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbSectorField implements DbFieldSchema {

	MinimumX("minx", "Minimum X", "Minimum X coordinate (inclusive) of sector."),
	MinimumY("miny", "Minimum Y", "Minimum Y coordinate (inclusive) of sector."),
	MinimumZ("minz", "Minimum Z", "Minimum Z coordinate (inclusive) of sector."),
	MaximumX("maxx", "Maximum X", "Maximum X coordinate (exclusive) of sector."),
	MaximumY("maxy", "Maximum Y", "Maximum Y coordinate (exclusive) of sector."),
	MaximumZ("maxz", "Maximum Z", "Maximum Z coordinate (exclusive) of sector.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbSectorField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbSectorField parse(String id) {
		for (DbSectorField property : DbSectorField.values()) {
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
