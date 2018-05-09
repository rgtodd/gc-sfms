package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Cluster REST service. These fields can appear
 * in sorting and filtering criteria.
 * 
 * These values correspond to the properties of the
 * {@link sfms.rest.api.models.Cluster} class.
 * 
 */
public enum ClusterField implements FieldSchema {

	Name("Key"),
	ClusterPartition("ClusterPartition"),
	ClusterX("ClusterX"),
	ClusterY("ClusterY"),
	ClusterZ("ClusterZ"),
	MinimumX("MinimumX"),
	MinimumY("MinimumY"),
	MinimumZ("MinimumZ"),
	MaximumX("MaximumX"),
	MaximumY("MaximumY"),
	MaximumZ("MaximumZ");

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

	@Override
	public String getName() {
		return m_name;
	}
}
