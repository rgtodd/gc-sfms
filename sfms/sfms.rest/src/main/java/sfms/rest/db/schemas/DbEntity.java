package sfms.rest.db.schemas;

public enum DbEntity {

	CrewMember("crew"), Spaceship("ship"), Star("star");

	private String m_kind;

	private DbEntity(String kind) {
		m_kind = kind;
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
}
