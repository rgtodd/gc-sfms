package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Spaceship REST service. These fields can
 * appear in sorting and filtering criteria.
 *
 * These values correspond to the properties of the
 * {@link sfms.rest.api.models.Spaceship} class.
 * 
 */
public enum SpaceshipField implements FieldSchema {

	Key("Key"),
	Name("Name"),
	X("X"),
	Y("Y"),
	Z("Z"),
	StarKey("StarKey");

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

	@Override
	public String getName() {
		return m_name;
	}
}
