package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Crew Member Mission entity.
 * 
 * @see DbEntity#CrewMemberMission
 */
public enum DbSpaceshipStateField implements DbFieldSchema {

	Timestamp("ts", DbValueType.Timestamp, "Timestamp", "Effective date/time for state information."),
	LocationEntity("dx", DbValueType.Key, "Location Entity", "Key of current location entity."),
	LocationX("x", DbValueType.Double, "X", "X coordinate of ship."),
	LocationY("x", DbValueType.Double, "Y", "Y coordinate of ship."),
	LocationZ("x", DbValueType.Double, "Z", "Z coordinate of ship."),
	Speed("s", DbValueType.Double, "Speed", "Speed of ship."),
	DestinationEntity("dx", DbValueType.Key, "Destination Entity", "Key of destination entity."),
	DestinationX("dx", DbValueType.Double, "Destination X", "X coordinate of destination."),
	DestinationY("dx", DbValueType.Double, "Destination Y", "Y coordinate of destination."),
	DestinationZ("dx", DbValueType.Double, "Destination Z", "Z coordinate of destination.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbSpaceshipStateField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbSpaceshipStateField parseName(String name) {
		for (DbSpaceshipStateField property : DbSpaceshipStateField.values()) {
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
