package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.Value;

import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.DbValueType;

/**
 * Defines the fields used by the Star entity.
 * 
 * @see DbEntity#Star
 */
public enum DbStarField implements DbFieldSchema {

	CatalogId("cat_id", DbValueType.Long, "Catalog ID", "ID of star in source catalog."),
	ClusterKey("cls_k", DbEntity.Cluster, "Cluster Key", "Key of parent Cluster entity."),
	SectorKey("sct_k", DbEntity.Sector, "Sector Key", "Key of parent Sector entity."),
	HipparcosId("hip", DbValueType.String, "Hipparcos ID", "ID of star in Hipparcos Catalog."),
	HenryDraperId("hd", DbValueType.String, "Henry Draper ID", "ID of star in Henry Draper Catalog."),
	HarvardRevisedId("hr", DbValueType.String, "Harvard Revised ID", "ID of star in Harvard Revised Catalog."),
	GlieseId("gl", DbValueType.String, "Gliese Catalog ID", "ID of star in Gliese Catalog of Nearby Stars."),
	BayerFlamsteedId("bf", DbValueType.String, "Bayer / Flamsteed Designation",
			"Bayer / Flamsteed designation, primarily from the Fifth Edition of the Yale Bright Star Catalog."),
	ProperName("p", DbValueType.String, "Proper Name",
			"A common name for the star taken primarily from the Hipparcos project's web site."),
	RightAscension("ra", DbValueType.Double, "Right Ascension", "Right ascension for epoch and equinox 2000.0."),
	Declination("dec", DbValueType.Double, "Declination", "Declination, for epoch and equinox 2000.0."),
	Distance("dist", DbValueType.Double, "Distance", "Distance from Earth in parsecs."),
	ProperMotionRightAscension("pmra", DbValueType.Double, "Proper Motion Right Ascension",
			"Right Ascension of proper motion in milliarcseconds per year."),
	ProperMotionDeclination("pmdec", DbValueType.Double, "Proper Motion Declination",
			"Declination of proper motion in milliarcseconds per year."),
	RadialVelocity("rv", DbValueType.Double, "Radial Velocity", "Radial velocity in km/sec."),
	Magnitude("mag", DbValueType.Double, "Magnitude", "Apparent visual magnitude."),
	AbsoluteMagnitude("absmag", DbValueType.Double, "Absolute Magnitude",
			"Apparent visual magnitude from a distance of 10 parsecs."),
	Spectrum("spect", DbValueType.String, "Spectral Type", "Spectral type."),
	ColorIndex("ci", DbValueType.String, "Color Index", "Color index (blue magnitude - visual magnitude)."),
	X("x", DbValueType.Double, "X",
			"X equatorial coordinate as seen from Earth where +X is in the direction of the vernal equinox (at epoch 2000)."),
	Y("y", DbValueType.Double, "Y",
			"Y equatorial coordinate as seen from Earth where +Y in the direction of R.A. 6 hours, declination 0 degrees."),
	Z("z", DbValueType.Double, "Z",
			"Z equatorial coordinate as seen from Earth where +Z is towards the north celestial pole."),
	VX("vx", DbValueType.Double, "VX", "Velocity of X in parsecs per year."),
	VY("vy", DbValueType.Double, "VY", "Velocity of Y in parsecs per year."),
	VZ("vz", DbValueType.Double, "VZ", "Velocity of Z in parsecs per year."),
	RightAcensionRadians("rarad", DbValueType.Double, "Right Ascension Radians", "Right ascension in radians."),
	DeclinationRadians("decrad", DbValueType.Double, "Declination Radians", "Declination in radians."),
	ProperMotionRightAscensionRadians("pmrarad", DbValueType.Double, "Proper Motion Right Ascension Radians",
			"Proper motion right ascension in radians."),
	ProperMotionDeclinationRadians("pmdecrad", DbValueType.Double, "Proper Motion Declination Radians",
			"Proper motion decliation in radians."),
	BayerId("bayer", DbValueType.String, "Bayer Desgination", "Bayer component of Bayer / Flamsteed designation."),
	Flamsteed("flam", DbValueType.String, "Flamsteed Designation",
			"Flamsteed component of Bayer / Flamsteed designation."),
	Constellation("con", DbValueType.String, "Constellation", "Standard constellation abbreviation."),
	CompanionStarId("comp", DbValueType.String, "Companion Star ID", "ID of companion star in a multiple star system."),
	PrimaryStarId("compp", DbValueType.String, "Primary Star ID", "ID of primary star in a multiple star system."),
	MultipleStarId("base", DbValueType.String, "Multiple Star ID", "Catalog ID or name for multiple star system."),
	Luminosity("lum", DbValueType.Double, "Luminosity", "Luminosity as a multiple of Solar luminosity."),
	VariableStarDesignation("var", DbValueType.String, "Variable Star Designation",
			"Standard variable star designation."),
	VariableMinimum("varmin", DbValueType.Double, "Variable Minimum",
			"Minimum magnitude adjusted to match scale of apparent visual magnitude."),
	VariableMaximum("varmax", DbValueType.Double, "Variable Maximum",
			"Maximum magnitude adjusted to match scale of apparent visual magnitude.");

	private String m_name;
	private DbValueType m_dbValueType;
	private DbEntity m_dbEntity;
	private String m_title;
	private String m_description;

	private DbStarField(String name, DbValueType dbValueType, String title, String description) {
		m_name = name;
		m_dbValueType = dbValueType;
		m_title = title;
		m_description = description;
	}

	private DbStarField(String name, DbEntity dbEntity, String title, String description) {
		m_name = name;
		m_dbEntity = dbEntity;
		m_title = title;
		m_description = description;
	}

	public static DbStarField parseName(String name) {
		for (DbStarField property : DbStarField.values()) {
			if (property.getName().equals(name)) {
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
	public Value<?> parseValue(Datastore datastore, String text) {
		if (m_dbValueType != null) {
			return m_dbValueType.parse(text);
		} else {
			return KeyValue.of(m_dbEntity.createEntityKey(datastore, text));
		}
	}
}
