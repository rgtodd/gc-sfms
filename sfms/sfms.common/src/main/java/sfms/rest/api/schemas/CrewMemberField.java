package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Crew Member REST service. These fields can
 * appear in sorting and filtering criteria.
 * 
 * These values correspond to the properties of the
 * {@link sfms.rest.api.models.CrewMember} class.
 * 
 */
public enum CrewMemberField implements FieldSchema {

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

	@Override
	public String getName() {
		return m_name;
	}
}
