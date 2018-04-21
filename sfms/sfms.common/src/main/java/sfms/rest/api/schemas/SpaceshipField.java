package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Spaceship REST service. These fields can
 * appear in sorting and filtering criteria.
 *
 */
public enum SpaceshipField {

	Key("Key"),
	Name("Name");

	private String m_name;

	private SpaceshipField(String name) {
		m_name = name;
	}

	public static SpaceshipField parse(String name) {
		for (SpaceshipField property : SpaceshipField.values()) {
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
