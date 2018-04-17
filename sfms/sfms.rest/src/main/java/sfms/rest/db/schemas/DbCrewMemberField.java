package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbCrewMemberField implements DbFieldSchema {

	FirstName("fn", "First Name", "First name of crew member."),
	LastName("ln", "Last Name", "Last name of crew member.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbCrewMemberField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbCrewMemberField parse(String id) {
		for (DbCrewMemberField property : DbCrewMemberField.values()) {
			if (property.getName().equals(id)) {
				return property;
			}
		}

		return null;
	}

	public String getName() {
		return m_id;
	}

	public String getTitle() {
		return m_name;
	}

	public String getDescription() {
		return m_description;
	}
}
