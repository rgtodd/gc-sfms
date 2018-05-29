package sfms.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

/**
 * Defines the entities managed by data store.
 */
public enum DbEntity {

	// ID Based Entities
	//
	CrewMember("Crew", DbCrewMemberField.class, true),
	Spaceship("Ship", DbSpaceshipField.class, true),
	SpaceStation("SpaceStation", DbSpaceStationField.class, true),

	// Name Based Entities
	//
	Cluster("Cluster", DbClusterField.class, false),
	CrewMemberState("CrwSte", DbCrewMemberStateField.class, false),
	Mission("Msn", DbMissionField.class, false),
	MissionState("MsnSte", DbMissionStateField.class, false),
	Sector("Sector", DbSectorField.class, false),
	SpaceshipState("ShpSte", DbSpaceshipStateField.class, false),
	Star("Star", DbStarField.class, false);

	private String m_kind;
	private Class<?> m_fieldSchema;
	private boolean m_isIdKey;

	private DbEntity(String kind, Class<?> fieldSchema, boolean isIdKey) {
		m_kind = kind;
		m_fieldSchema = fieldSchema;
		m_isIdKey = isIdKey;
	}

	public static DbEntity parse(String kind) {
		for (DbEntity property : DbEntity.values()) {
			if (property.getKind().equals(kind)) {
				return property;
			}
		}

		return null;
	}

	public String getKind() {
		return m_kind;
	}

	public Class<?> getFieldSchema() {
		return m_fieldSchema;
	}

	public boolean isIdKey() {
		return m_isIdKey;
	}

	public Key createEntityKey(Datastore datastore, String restKey) {
		if (restKey == null)
			return null;
		if (isIdKey()) {
			return datastore.newKeyFactory().setKind(getKind()).newKey(Long.parseLong(restKey));
		} else {
			return datastore.newKeyFactory().setKind(getKind()).newKey(restKey);
		}
	}

	public String createRestKey(Key key) {
		if (key == null)
			return null;
		if (isIdKey()) {
			return key.getId().toString();
		} else {
			return key.getName();
		}
	}
}
