package sfms.rest.db.schemas;

import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Cluster Sector entity.
 * 
 * @see DbEntity#ClusterSector
 */
public enum DbClusterSectorField implements DbFieldSchema {

	ClusterKey("cls_k", DbValueType.Key, "Cluster Key", "Key of parent Cluster entity ."),
	SectorKey("sct_k", DbValueType.Key, "Sector Key", "Key of Sector entity contained in this cluster.");

	private String m_name;
	private DbValueType m_dbValueType;
	private String m_title;
	private String m_description;

	private DbClusterSectorField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
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
		return m_dbValueType.parse(text);
	}
}
