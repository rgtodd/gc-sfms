package sfms.rest.api.schemas;

/**
 * Defines data fields used by the Star REST service. These fields can appear in
 * sorting and filtering criteria.
 *
 * These values correspond to the properties of the
 * {@link sfms.rest.api.models.Star} class.
 * 
 */
public enum StarField implements FieldSchema {

	Key("Key"),
	CatalogId("CatalogId"),
	ClusterKey("ClusterKey"),
	SectorKey("SectorKey"),
	HipparcosId("HipparcosId"),
	HenryDraperId("HenryDraperId"),
	HarvardRevisedId("HarvardRevisedId"),
	GlieseId("GlieseId"),
	BayerFlamsteedId("BayerFlamsteedId"),
	ProperName("ProperName"),
	RightAscension("RightAscension"),
	Declination("Declination"),
	Distance("Distance"),
	ProperMotionRightAscension("ProperMotionRightAscension"),
	ProperMotionDeclination("ProperMotionDeclination"),
	RadialVelocity("RadialVelocity"),
	Magnitude("Magnitude"),
	AbsoluteMagnitude("AbsoluteMagnitude"),
	Spectrum("Spectrum"),
	ColorIndex("ColorIndex"),
	X("X"),
	Y("Y"),
	Z("Z"),
	VX("VX"),
	VY("VY"),
	VZ("VZ"),
	RightAcensionRadians("RightAcensionRadians"),
	DeclinationRadians("DeclinationRadians"),
	ProperMotionRightAscensionRadians("ProperMotionRightAscensionRadians"),
	ProperMotionDeclinationRadians("ProperMotionDeclinationRadians"),
	BayerId("BayerId"),
	Flamsteed("Flamsteed"),
	Constellation("Constellation"),
	CompanionStarId("CompanionStarId"),
	PrimaryStarId("PrimaryStarId"),
	MultipleStarId("MultipleStarId"),
	Luminosity("Luminosity"),
	VariableStarDesignation("VariableStarDesignation"),
	VariableMinimum("VariableMinimum"),
	VariableMaximum("VariableMaximum");

	private String m_name;

	private StarField(String name) {
		m_name = name;
	}

	public static StarField parse(String name) {
		for (StarField property : StarField.values()) {
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
}
