package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Sector entity.
 * 
 * @see DbEntity#Sector
 */
public enum DbSectorField implements DbFieldSchema {

	SectorX("x", DbValueType.Long, "X", "X index of sector."),
	SectorY("y", DbValueType.Long, "Y", "Y index of sector."),
	SectorZ("z", DbValueType.Long, "Z", "Z index of sector."),
	MinimumX("min_x", DbValueType.Long, "Minimum X", "Minimum X coordinate (inclusive) of sector."),
	MinimumY("min_y", DbValueType.Long, "Minimum Y", "Minimum Y coordinate (inclusive) of sector."),
	MinimumZ("min_z", DbValueType.Long, "Minimum Z", "Minimum Z coordinate (inclusive) of sector."),
	MaximumX("max_x", DbValueType.Long, "Maximum X", "Maximum X coordinate (exclusive) of sector."),
	MaximumY("max_y", DbValueType.Long, "Maximum Y", "Maximum Y coordinate (exclusive) of sector."),
	MaximumZ("max_z", DbValueType.Long, "Maximum Z", "Maximum Z coordinate (exclusive) of sector.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbSectorField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbSectorField parseName(String name) {
		for (DbSectorField property : DbSectorField.values()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public String getTitle() {
		return m_title;
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public Value<?> parseValue(Datastore datastore, String text) {
		return m_dbValueType.parse(text);
	}
}
