package sfms.rest.api.schemas;

public enum StarshipField {

	Key("Key"),
	Name("Name");

	private String m_name;

	private StarshipField(String name) {
		m_name = name;
	}

	public static StarshipField parse(String name) {
		for (StarshipField property : StarshipField.values()) {
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
