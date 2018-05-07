package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.KeyValue;
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
	StarKey("str_k", DbEntity.Star, "Star Key", "Key of Star entity locating the space station.");

	private String m_name;
	private String m_title;
	private String m_description;
	private DbValueType m_dbValueType;
	private DbEntity m_dbEntity;

	private DbSpaceStationField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	private DbSpaceStationField(String name, DbEntity dbEntity, String title, String description) {
		m_name = name;
		m_dbEntity = dbEntity;
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
	public Value<?> parseValue(Datastore datastore, String text) {
		if (m_dbValueType != null) {
			return m_dbValueType.parse(text);
		} else {
			return KeyValue.of(m_dbEntity.createEntityKey(datastore, text));
		}
	}
}
