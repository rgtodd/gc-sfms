package sfms.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.db.DbFieldSchema;
import sfms.db.DbValueType;

/**
 * Defines the fields used by the Spaceship State entity.
 * 
 * @see DbEntity#SpaceshipState
 */
public enum DbSpaceshipStateField implements DbFieldSchema {

	Timestamp("ts", DbValueType.Timestamp, "Timestamp", "Effective timestamp for state information."),

	// Intrinsic properties
	//
	LocationX("x", DbValueType.Double, "X", "X coordinate of ship."),
	LocationY("y", DbValueType.Double, "Y", "Y coordinate of ship."),
	LocationZ("z", DbValueType.Double, "Z", "Z coordinate of ship."),
	LocationKey("lk", DbValueType.Key, "Location Entity", "Key of entity associated with current location."),
	LocationArrivalTimestamp("la", DbValueType.Timestamp, "Location Arrival",
			"Arrival timestamp for current location."),
	Distance("d", DbValueType.Double, "Distance", "Distance travelled."),

	// Spaceship movement properties
	//
	Speed("s", DbValueType.Double, "Speed", "Speed of ship."),
	DestinationX("dx", DbValueType.Double, "Destination X", "X coordinate of destination."),
	DestinationY("dy", DbValueType.Double, "Destination Y", "Y coordinate of destination."),
	DestinationZ("dz", DbValueType.Double, "Destination Z", "Z coordinate of destination."),
	DestinationKey("dk", DbValueType.Key, "Destination Entity", "Key of entity associated with destination.");

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
