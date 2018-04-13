package sfms.rest.api.schemas;

public enum SectorField {

	Name("Key"),
	MinimumX("MinimumX"),
	MinimumY("MinimumY"),
	MinimumZ("MinimumZ"),
	MaximumX("MaximumX"),
	MaximumY("MaximumY"),
	MaximumZ("MaximumZ"),
	Stars("Stars");

	private String m_name;

	private SectorField(String name) {
		m_name = name;
	}

	public static SectorField parse(String name) {
		for (SectorField property : SectorField.values()) {
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
