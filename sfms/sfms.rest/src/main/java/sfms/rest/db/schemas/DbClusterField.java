package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbClusterField implements DbFieldSchema {

	ClusterPartition("p", "Partition", "Partition of cluster."),
	ClusterX("x", "X", "X index of cluster."),
	ClusterY("y", "Y", "Y index of cluster."),
	ClusterZ("z", "Z", "Z index of cluster."),
	MinimumX("min_x", "Minimum X", "Minimum X coordinate (inclusive) of cluster."),
	MinimumY("min_y", "Minimum Y", "Minimum Y coordinate (inclusive) of cluster."),
	MinimumZ("min_z", "Minimum Z", "Minimum Z coordinate (inclusive) of cluster."),
	MaximumX("max_x", "Maximum X", "Maximum X coordinate (exclusive) of cluster."),
	MaximumY("max_y", "Maximum Y", "Maximum Y coordinate (exclusive) of cluster."),
	MaximumZ("max_z", "Maximum Z", "Maximum Z coordinate (exclusive) of cluster.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbClusterField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbClusterField parse(String id) {
		for (DbClusterField property : DbClusterField.values()) {
			if (property.getName().equals(id)) {
				return property;
			}
		}

		return null;
	}

	public String getName() {
		return m_id;
	}

	public String getTitle() {
		return m_name;
	}

	public String getDescription() {
		return m_description;
	}
}
