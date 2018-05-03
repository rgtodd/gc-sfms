package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Cluster Star entity.
 * 
 * @see DbEntity#ClusterStar
 */
public enum DbClusterStarField implements DbFieldSchema {

	ClusterKey("cls_k", DbValueType.Key, "Cluster Key", "Key of parent Cluster entity ."),
	StarKey("str_k", DbValueType.Key, "Star Key", "Key of Star entity contained in this cluster.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbClusterStarField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	public static DbClusterStarField parseName(String name) {
		for (DbClusterStarField property : DbClusterStarField.values()) {
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
