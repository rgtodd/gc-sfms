package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Space Station entity.
 * 
 * @see DbEntity#SpaceStation
 */
public enum DbSpaceStationField implements DbFieldSchema {

	Name("n", DbValueType.String, "Name", "Name of space station."),
	StarKey("str_k", DbValueType.Key, "Star Key", "Key of Star entity locating the space station.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbSpaceStationField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbSpaceStationField parseName(String name) {
		for (DbSpaceStationField property : DbSpaceStationField.values()) {
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
	public Value<?> parseValue(String text) {
		return m_dbValueType.parse(text);
	}
}
