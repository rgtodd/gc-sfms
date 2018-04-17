package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbSectorField implements DbFieldSchema {

	SectorX("x", "X", "X index of sector."),
	SectorY("y", "Y", "Y index of sector."),
	SectorZ("z", "Z", "Z index of sector."),
	MinimumX("min_x", "Minimum X", "Minimum X coordinate (inclusive) of sector."),
	MinimumY("min_y", "Minimum Y", "Minimum Y coordinate (inclusive) of sector."),
	MinimumZ("min_z", "Minimum Z", "Minimum Z coordinate (inclusive) of sector."),
	MaximumX("max_x", "Maximum X", "Maximum X coordinate (exclusive) of sector."),
	MaximumY("max_y", "Maximum Y", "Maximum Y coordinate (exclusive) of sector."),
	MaximumZ("max_z", "Maximum Z", "Maximum Z coordinate (exclusive) of sector.");

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
