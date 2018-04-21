package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;

public enum DbSpaceStationField implements DbFieldSchema {

	Name("n", "Name", "Name of space station."),
	StarKey("str_k", "Star Key", "Key of Star entity locating the space station.");

	private String m_name;
	private String m_title;
	private String m_description;

	private DbSpaceStationField(String name, String title, String description) {
		m_name = name;
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
		return null;
	}
}
