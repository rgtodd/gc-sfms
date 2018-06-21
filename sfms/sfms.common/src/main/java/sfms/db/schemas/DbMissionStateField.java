package sfms.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.db.DbFieldSchema;
import sfms.db.DbValueType;

/**
 * Defines the fields used by the Mission State entity.
 * 
 * @see DbEntity#MissionState
 */
public enum DbMissionStateField implements DbFieldSchema {

	Timestamp("ts", DbValueType.Timestamp, "Timestamp", "Effective date/time for state information."),
	ObjectiveIndex("oi", DbValueType.String, "Objective Index", "Current mission objective index."),
	StartTimestamp("st", DbValueType.Timestamp, "Start Date/Time", "Objective start date/time."),
	EndTimestamp("et", DbValueType.Timestamp, "End Date/Time", "Objective end date/time.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbMissionStateField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbMissionStateField parseName(String name) {
		for (DbMissionStateField property : DbMissionStateField.values()) {
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
