package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;

public enum DbCrewMemberField implements DbFieldSchema {

	FirstName("fn", "First Name", "First name of crew member."),
	LastName("ln", "Last Name", "Last name of crew member.");

	private String m_name;
	private String m_title;
	private String m_description;

	private DbCrewMemberField(String name, String title, String description) {
		m_name = name;
		m_title = title;
		m_description = description;
	}

	public static DbCrewMemberField parseName(String name) {
		for (DbCrewMemberField property : DbCrewMemberField.values()) {
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
