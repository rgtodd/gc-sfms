package sfms.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Value;

import sfms.db.DbFieldSchema;
import sfms.db.DbValueType;

/**
 * Defines the fields used by the Simulation entity.
 * 
 * @see DbEntity#Simulation
 */
public enum DbSimulationField implements DbFieldSchema {

	Timestamp("ts", DbValueType.Timestamp, "Timestamp", "Date/time of the simulation.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbSimulationField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbSimulationField parseName(String name) {
		for (DbSimulationField property : DbSimulationField.values()) {
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
