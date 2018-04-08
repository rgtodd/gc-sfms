package sfms.rest.db.schemas;

public enum DbEntity {

	Cluster("Cluster", DbClusterField.class),
	ClusterSector("ClusterSector", DbClusterSectorField.class),
	ClusterStar("ClusterStar", DbClusterStarField.class),
	CrewMember("Crew", DbCrewMemberField.class),
	Sector("Sector", DbSectorField.class),
	SectorStar("SectorStar", DbSectorStarField.class),
	Spaceship("Ship", DbSpaceshipField.class),
	SpaceStation("SpaceStation", DbSpaceStationField.class),
	Star("Star", DbStarField.class);

	private String m_kind;
	private Class<?> m_fieldSchema;

	private DbEntity(String kind, Class<?> fieldSchema) {
		m_kind = kind;
		m_fieldSchema = fieldSchema;
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
}
