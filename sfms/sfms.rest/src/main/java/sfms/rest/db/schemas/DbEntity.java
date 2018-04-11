package sfms.rest.db.schemas;

import java.util.function.Function;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

public enum DbEntity {

	// ID Based Entities
	//
	CrewMember("Crew", DbCrewMemberField.class, DbEntity::IdKeyRestProvider, DbEntity::IdRestKeyFactory),
	Spaceship("Ship", DbSpaceshipField.class, DbEntity::IdKeyRestProvider, DbEntity::IdRestKeyFactory),
	SpaceStation("SpaceStation", DbSpaceStationField.class, DbEntity::IdKeyRestProvider, DbEntity::IdRestKeyFactory),

	// Name Based Entities
	//
	Cluster("Cluster", DbClusterField.class, DbEntity::NameKeyRestProvider, DbEntity::NameRestKeyFactory),
	ClusterSector("ClusterSector", DbClusterSectorField.class, DbEntity::NameKeyRestProvider,
			DbEntity::NameRestKeyFactory),
	ClusterStar("ClusterStar", DbClusterStarField.class, DbEntity::NameKeyRestProvider, DbEntity::NameRestKeyFactory),
	Sector("Sector", DbSectorField.class, DbEntity::NameKeyRestProvider, DbEntity::NameRestKeyFactory),
	SectorStar("SectorStar", DbSectorStarField.class, DbEntity::NameKeyRestProvider, DbEntity::NameRestKeyFactory),
	Star("Star", DbStarField.class, DbEntity::NameKeyRestProvider, DbEntity::NameRestKeyFactory);

	private String m_kind;
	private Class<?> m_fieldSchema;
	private Function<Key, String> m_restKeyProvider;
	private TriFunction<Datastore, String, String, Key> m_restKeyFactory;

	private DbEntity(String kind, Class<?> fieldSchema, Function<Key, String> restKeyProvider,
			TriFunction<Datastore, String, String, Key> restKeyFactory) {
		m_kind = kind;
		m_fieldSchema = fieldSchema;
		m_restKeyProvider = restKeyProvider;
		m_restKeyFactory = restKeyFactory;
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

	public Function<Key, String> getRestKeyProvider() {
		return m_restKeyProvider;
	}

	public TriFunction<Datastore, String, String, Key> getRestKeyFactory() {
		return m_restKeyFactory;
	}

	private static String IdKeyRestProvider(Key key) {
		if (key == null)
			return null;
		return String.valueOf(key.getId());
	}

	private static String NameKeyRestProvider(Key key) {
		if (key == null)
			return null;
		return key.getName();
	}

	private static Key IdRestKeyFactory(Datastore datastore, String kind, String id) {
		return datastore.newKeyFactory().setKind(kind).newKey(Long.parseLong(id));
	}

	private static Key NameRestKeyFactory(Datastore datastore, String kind, String id) {
		return datastore.newKeyFactory().setKind(kind).newKey(id);
	}
}
