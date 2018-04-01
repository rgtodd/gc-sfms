package sfms.rest.db.schemas;

public enum DbCrewMemberField {

	FirstName("fn"), LastName("ln");

	private String m_name;

	private DbCrewMemberField(String name) {
		m_name = name;
	}

	public static DbCrewMemberField parse(String name) {
		for (DbCrewMemberField property : DbCrewMemberField.values()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		return null;
	}

	public String getName() {
		return m_name;
	}
}
