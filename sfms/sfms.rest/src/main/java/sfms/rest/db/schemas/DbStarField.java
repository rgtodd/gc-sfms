package sfms.rest.db.schemas;

import sfms.rest.db.DbFieldSchema;

public enum DbStarField implements DbFieldSchema {

	ClusterKey("ckey", "Cluster Key", "Key of Cluster entity containing this star and its nearest neighbors."),
	HipparcosId("hip", "Hipparcos ID", "ID of star in Hipparcos Catalog."),
	HenryDraperId("hd", "Henry Draper ID", "ID of star in Henry Draper Catalog."),
	HarvardRevisedId("hr", "Harvard Revised ID", "ID of star in Harvard Revised Catalog."),
	GlieseId("gl", "Gliese Catalog ID", "ID of star in Gliese Catalog of Nearby Stars."),
	BayerFlamsteedId("bf", "Bayer / Flamsteed Designation",
			"Bayer / Flamsteed designation, primarily from the Fifth Edition of the Yale Bright Star Catalog."),
	ProperName("p", "Proper Name", "A common name for the star taken primarily from the Hipparcos project's web site."),
	RightAscension("ra", "Right Ascension", "Right ascension for epoch and equinox 2000.0."),
	Declination("dec", "Declination", "Declination, for epoch and equinox 2000.0."),
	Distance("dist", "Distance", "Distance from Earth in parsecs."),
	ProperMotionRightAscension("pmra", "Proper Motion Right Ascension",
			"Right Ascension of proper motion in milliarcseconds per year."),
	ProperMotionDeclination("pmdec", "Proper Motion Declination",
			"Declination of proper motion in milliarcseconds per year."),
	RadialVelocity("rv", "Radial Velocity", "Radial velocity in km/sec."),
	Magnitude("mag", "Magnitude", "Apparent visual magnitude."),
	AbsoluteMagnitude("absmag", "Absolute Magnitude", "Apparent visual magnitude from a distance of 10 parsecs."),
	Spectrum("spect", "Spectral Type", "Spectral type."),
	ColorIndex("ci", "Color Index", "Color index (blue magnitude - visual magnitude)."),
	X("x", "X",
			"X equatorial coordinate as seen from Earth where +X is in the direction of the vernal equinox (at epoch 2000)."),
	Y("y", "Y",
			"Y equatorial coordinate as seen from Earth where +Y in the direction of R.A. 6 hours, declination 0 degrees."),
	Z("z", "Z", "Z equatorial coordinate as seen from Earth where +Z is towards the north celestial pole."),
	VX("vx", "VX", "Velocity of X in parsecs per year."),
	VY("vy", "VY", "Velocity of Y in parsecs per year."),
	VZ("vz", "VZ", "Velocity of Z in parsecs per year."),
	RightAcensionRadians("rarad", "Right Ascension Radians", "Right ascension in radians."),
	DeclinationRadians("decrad", "Declination Radians", "Declination in radians."),
	ProperMotionRightAscensionRadians("pmrarad", "Proper Motion Right Ascension Radians",
			"Proper motion right ascension in radians."),
	ProperMotionDeclinationRadians("pmdecrad", "Proper Motion Declination Radians",
			"Proper motion decliation in radians."),
	BayerId("bayer", "Bayer Desgination", "Bayer component of Bayer / Flamsteed designation."),
	Flamsteed("flam", "Flamsteed Designation", "Flamsteed component of Bayer / Flamsteed designation."),
	Constellation("con", "Constellation", "Standard constellation abbreviation."),
	CompanionStarId("comp", "Companion Star ID", "ID of companion star in a multiple star system."),
	PrimaryStarId("compp", "Primary Star ID", "ID of primary star in a multiple star system."),
	MultipleStarId("base", "Multiple Star ID", "Catalog ID or name for multiple star system."),
	Luminosity("lum", "Luminosity", "Luminosity as a multiple of Solar luminosity."),
	VariableStarDesignation("var", "Variable Star Designation", "Standard variable star designation."),
	VariableMinimum("varmin", "Variable Minimum",
			"Minimum magnitude adjusted to match scale of apparent visual magnitude."),
	VariableMaximum("varmax", "Variable Maximum",
			"Maximum magnitude adjusted to match scale of apparent visual magnitude.");

	private String m_id;
	private String m_name;
	private String m_description;

	private DbStarField(String id, String name, String description) {
		m_id = id;
		m_name = name;
		m_description = description;
	}

	public static DbStarField parse(String id) {
		for (DbStarField property : DbStarField.values()) {
			if (property.getId().equals(id)) {
				return property;
			}
		}

		return null;
	}

	public String getId() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public String getDescription() {
		return m_description;
	}
}
