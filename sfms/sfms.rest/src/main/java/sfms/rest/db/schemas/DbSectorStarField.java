package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;

public enum DbSectorStarField implements DbFieldSchema {

	SectorKey("sct_k", "Sector Key", "Key of parent Sector entity."),
	StarKey("str_k", "Star Key", "Key of Star entity contained in this sector.");

	private String m_name;
	private String m_title;
	private String m_description;

	private DbSectorStarField(String name, String title, String description) {
		m_name = name;
		m_title = title;
		m_description = description;
	}

	public static DbSectorStarField parseName(String name) {
		for (DbSectorStarField property : DbSectorStarField.values()) {
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
