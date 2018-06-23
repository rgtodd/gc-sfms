package sfms.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.db.DbFieldSchema;
import sfms.db.DbValueType;

/**
 * Defines the fields used by the Mission entity.
 * 
 * Mission keys are of the form:
 * 
 * <EntityType>#<EntityKey>#<ReverseTimestamp>
 * 
 * @see DbEntity#Mission
 */
public enum DbMissionField implements DbFieldSchema {

	MissionDefinition("m", DbValueType.String, "Mission", "Mission definition in JSON format."),
	StartTimestamp("st", DbValueType.Timestamp, "Start Date/Time", "Mission start date/time."),
	EndTimestamp("et", DbValueType.Timestamp, "End Date/Time", "Mission end date/time."),
	MissionStatus("ms", DbValueType.String, "Mission Status", "Indicates if mission is active or complete.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbMissionField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbMissionField parseName(String name) {
		for (DbMissionField property : DbMissionField.values()) {
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
