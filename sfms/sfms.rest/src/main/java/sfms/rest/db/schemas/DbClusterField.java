package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Cluster entity.
 * 
 * @see DbEntity#Cluster
 */
public enum DbClusterField implements DbFieldSchema {

	ClusterPartition("p", DbValueType.Long, "Partition", "Partition of cluster."),
	ClusterX("x", DbValueType.Long, "X", "X index of cluster."),
	ClusterY("y", DbValueType.Long, "Y", "Y index of cluster."),
	ClusterZ("z", DbValueType.Long, "Z", "Z index of cluster."),
	MinimumX("min_x", DbValueType.Long, "Minimum X", "Minimum X coordinate (inclusive) of cluster."),
	MinimumY("min_y", DbValueType.Long, "Minimum Y", "Minimum Y coordinate (inclusive) of cluster."),
	MinimumZ("min_z", DbValueType.Long, "Minimum Z", "Minimum Z coordinate (inclusive) of cluster."),
	MaximumX("max_x", DbValueType.Long, "Maximum X", "Maximum X coordinate (exclusive) of cluster."),
	MaximumY("max_y", DbValueType.Long, "Maximum Y", "Maximum Y coordinate (exclusive) of cluster."),
	MaximumZ("max_z", DbValueType.Long, "Maximum Z", "Maximum Z coordinate (exclusive) of cluster.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbClusterField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbClusterField parseName(String name) {
		for (DbClusterField property : DbClusterField.values()) {
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

	@Override
	public String getTitle() {
		return m_title;
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public Value<?> parseValue(String text) {
		return m_dbValueType.parse(text);
	}
}
