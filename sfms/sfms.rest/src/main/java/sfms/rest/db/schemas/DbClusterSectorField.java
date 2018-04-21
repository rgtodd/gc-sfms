package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;

public enum DbClusterSectorField implements DbFieldSchema {

	ClusterKey("cls_k", "Cluster Key", "Key of parent Cluster entity ."),
	SectorKey("sct_k", "Sector Key", "Key of Sector entity contained in this cluster.");

	private String m_name;
	private String m_title;
	private String m_description;

	private DbClusterSectorField(String name, String title, String description) {
		m_name = name;
		m_title = title;
		m_description = description;
	}

	public static DbClusterSectorField parse(String id) {
		for (DbClusterSectorField property : DbClusterSectorField.values()) {
			if (property.getName().equals(id)) {
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
		return null;
	}
}
