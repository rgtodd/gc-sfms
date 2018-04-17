package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbClusterStarField implements DbFieldSchema {

	ClusterKey("cls_k", "Cluster Key", "Key of parent Cluster entity ."),
	StarKey("str_k", "Star Key", "Key of Star entity contained in this cluster.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbClusterStarField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbClusterStarField parse(String id) {
		for (DbClusterStarField property : DbClusterStarField.values()) {
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
