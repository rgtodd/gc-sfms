package sfms.rest.db.schemas;

public enum DbEntity {

	Cluster("cluster", DbClusterField.class),
	ClusterSector("clustersector", DbClusterSectorField.class),
	CrewMember("crew", DbCrewMemberField.class),
	Sector("sector", DbSectorField.class),
	SectorStar("sector-star", DbSectorStarField.class),
	Spaceship("ship", DbSpaceshipField.class),
	SpaceStation("spacestation", DbSpaceStationField.class),
	Star("star", DbStarField.class);

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
