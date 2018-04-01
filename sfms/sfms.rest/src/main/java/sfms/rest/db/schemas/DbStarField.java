package sfms.rest.db.schemas;

public enum DbStarField {

	StarId("starId"), ProperName("properName");

	private String m_name;

	private DbStarField(String name) {
		m_name = name;
	}

	public static DbStarField parse(String name) {
		for (DbStarField property : DbStarField.values()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		return null;
	}

	public String getName() {
		return m_name;
	}

}
