package sfms.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.db.DbFieldSchema;
import sfms.db.DbValueType;

/**
 * Defines the fields used by the Crew Member State entity.
 * 
 * @see DbEntity#CrewMemberState
 */
public enum DbCrewMemberStateField implements DbFieldSchema {

	Timestamp("ts", DbValueType.Timestamp, "Timestamp", "Effective timestamp for state information."),

	// Intrinsic properties
	//
	LocationKey("lk", DbValueType.Key, "Location Entity", "Key of entity associated with current location."),
	LocationArrivalTimestamp("la", DbValueType.Timestamp, "Location Arrival",
			"Arrival timestamp for current location."),

	// Crew member movement properties
	//
	DestinationKey("dk", DbValueType.Key, "Destination Entity", "Key of entity associated with destination.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbCrewMemberStateField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbCrewMemberStateField parseName(String name) {
		for (DbCrewMemberStateField property : DbCrewMemberStateField.values()) {
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
