package sfms.rest.api.schemas;

public enum CrewMemberField {

	FirstName("FirstName"), LastName("LastName");

	private String m_name;

	private CrewMemberField(String name) {
		m_name = name;
	}

	public static CrewMemberField parse(String name) {
		for (CrewMemberField property : CrewMemberField.values()) {
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
