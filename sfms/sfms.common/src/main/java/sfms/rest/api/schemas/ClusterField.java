package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Cluster REST service. These fields can appear
 * in sorting and filtering criteria.
 *
 */
public enum ClusterField {

	Name("Key"),
	MinimumX("MinimumX"),
	MinimumY("MinimumY"),
	MinimumZ("MinimumZ"),
	MaximumX("MaximumX"),
	MaximumY("MaximumY"),
	MaximumZ("MaximumZ"),
	Stars("Stars");

	private String m_name;

	private ClusterField(String name) {
		m_name = name;
	}

	public static ClusterField parse(String name) {
		for (ClusterField property : ClusterField.values()) {
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
