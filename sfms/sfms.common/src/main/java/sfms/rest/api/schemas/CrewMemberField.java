package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Crew Member REST service. These fields can
 * appear in sorting and filtering criteria.
 *
 */
public enum CrewMemberField {

	Key("Key"),
	FirstName("FirstName"),
	LastName("LastName");

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
