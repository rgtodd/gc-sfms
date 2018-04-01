package sfms.rest.db.schemas;

public enum DbStarField {

	HipparcosId("hip"),
	HenryDraperId("hd"),
	HarvardRevisedId("hr"),
	GlieseId("gl"),
	BayerFlamsteedId("bf"),
	ProperName("p"),
	RightAscension("ra"),
	Declination("dec"),
	Distance("dist"),
	ProperMotionRightAscension("pmra"),
	ProperMotionDeclination("pmdec"),
	RadialVelocity("rv"),
	Magnitude("mag"),
	AbsoluteMagnitude("absmag"),
	Spectrum("spect"),
	ColorIndex("ci"),
	X("x"),
	Y("y"),
	Z("z"),
	VX("vx"),
	VY("vy"),
	VZ("vz"),
	RightAcensionRadians("rarad"),
	DeclinationRadians("decrad"),
	ProperMotionRightAscensionRadians("pmrarad"),
	ProperMotionDeclinationRadians("pmdecrad"),
	BayerId("bayer"),
	Flamsteed("flam"),
	Constellation("con"),
	CompanionStarId("comp"),
	PrimaryStarId("compp"),
	MultipleStarId("base"),
	Luminosity("lum"),
	VariableStarDesignation("var"),
	VariableMinimum("varmin"),
	VariableMaximum("varmax");

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
