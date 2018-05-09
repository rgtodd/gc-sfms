package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Sector REST service. These fields can appear
 * in sorting and filtering criteria.
 *
 * These values correspond to the properties of the
 * {@link sfms.rest.api.models.Sector} class.
 * 
 */
public enum SectorField implements FieldSchema {

	Name("Key"),
	SectorX("SectorX"),
	SectorY("SectorY"),
	SectorZ("SectorZ"),
	MinimumX("MinimumX"),
	MinimumY("MinimumY"),
	MinimumZ("MinimumZ"),
	MaximumX("MaximumX"),
	MaximumY("MaximumY"),
	MaximumZ("MaximumZ");

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

	@Override
	public String getName() {
		return m_name;
	}
}
