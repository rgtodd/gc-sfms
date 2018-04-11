package sfms.rest.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Star {

	private String m_key; // StarID-id *KEY*
	private String m_clusterKey;
	private String m_sectorKey;
	private String m_hipparcosId; // hip
	private String m_henryDraperId; // HD-hd
	private String m_harvardRevisedId; // HR-hr
	private String m_glieseId; // Gliese-gl
	private String m_bayerFlamsteedId; // BayerFlamsteed-bf
	private String m_properName; // ProperName-proper
	private double m_rightAscension; // RA-ra
	private double m_declination; // Dec-dec
	private double m_distance; // Distance-dist
	private double m_properMotionRightAscension; // pmra
	private double m_properMotionDeclination; // pmdec
	private double m_radialVelocity; // rv
	private double m_magnitude; // Mag-mag
	private double m_absoluteMagnitude; // AbsMag-absmag
	private String m_spectrum; // Spectrum-spect
	private Double m_colorIndex; // ColorIndex-ci
	private double m_x; // x
	private double m_y; // y
	private double m_z; // z
	private double m_vx; // vx
	private double m_vy; // vy
	private double m_vz; // vz
	private double m_rightAcensionRadians; // rarad
	private double m_declinationRadians; // decrad
	private double m_properMotionRightAscensionRadians; // pmrarad
	private double m_properMotionDeclinationRadians; // prdecrad
	private String m_bayerId; // bayer
	private String m_flamsteed; // flam
	private String m_constellation; // con
	private String m_companionStarId; // comp
	private String m_primaryStarId; // comp_primary
	private String m_multipleStarId; // base
	private double m_luminosity; // lum
	private String m_variableStarDesignation; // var
	private Double m_variableMinimum; // var_min
	private Double m_variableMaximum; // var max

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public String getClusterKey() {
		return m_clusterKey;
	}

	public void setClusterKey(String clusterKey) {
		m_clusterKey = clusterKey;
	}

	public String getSectorKey() {
		return m_sectorKey;
	}

	public void setSectorKey(String sectorKey) {
		m_sectorKey = sectorKey;
	}

	public String getHipparcosId() {
		return m_hipparcosId;
	}

	public void setHipparcosId(String hipparcosId) {
		m_hipparcosId = hipparcosId;
	}

	public String getHenryDraperId() {
		return m_henryDraperId;
	}

	public void setHenryDraperId(String henryDraperId) {
		m_henryDraperId = henryDraperId;
	}

	public String getHarvardRevisedId() {
		return m_harvardRevisedId;
	}

	public void setHarvardRevisedId(String harvardRevisedId) {
		m_harvardRevisedId = harvardRevisedId;
	}

	public String getGlieseId() {
		return m_glieseId;
	}

	public void setGlieseId(String glieseId) {
		m_glieseId = glieseId;
	}

	public String getBayerFlamsteedId() {
		return m_bayerFlamsteedId;
	}

	public void setBayerFlamsteedId(String bayerFlamsteedId) {
		m_bayerFlamsteedId = bayerFlamsteedId;
	}

	public double getRightAscension() {
		return m_rightAscension;
	}

	public void setRightAscension(double rightAscension) {
		m_rightAscension = rightAscension;
	}

	public double getDeclination() {
		return m_declination;
	}

	public void setDeclination(double declination) {
		m_declination = declination;
	}

	public String getProperName() {
		return m_properName;
	}

	public void setProperName(String propertName) {
		m_properName = propertName;
	}

	public double getDistance() {
		return m_distance;
	}

	public void setDistance(double distance) {
		m_distance = distance;
	}

	public double getProperMotionRightAscension() {
		return m_properMotionRightAscension;
	}

	public void setProperMotionRightAscension(double properMotionRightAscension) {
		m_properMotionRightAscension = properMotionRightAscension;
	}

	public double getProperMotionDeclination() {
		return m_properMotionDeclination;
	}

	public void setProperMotionDeclination(double properMotionDeclination) {
		m_properMotionDeclination = properMotionDeclination;
	}

	public double getRadialVelocity() {
		return m_radialVelocity;
	}

	public void setRadialVelocity(double radialVelocity) {
		m_radialVelocity = radialVelocity;
	}

	public double getMagnitude() {
		return m_magnitude;
	}

	public void setMagnitude(double magnitude) {
		m_magnitude = magnitude;
	}

	public double getAbsoluteMagnitude() {
		return m_absoluteMagnitude;
	}

	public void setAbsoluteMagnitude(double absoluteMagnitude) {
		m_absoluteMagnitude = absoluteMagnitude;
	}

	public String getSpectrum() {
		return m_spectrum;
	}

	public void setSpectrum(String spectrum) {
		m_spectrum = spectrum;
	}

	public Double getColorIndex() {
		return m_colorIndex;
	}

	public void setColorIndex(Double colorIndex) {
		m_colorIndex = colorIndex;
	}

	public double getX() {
		return m_x;
	}

	public void setX(double x) {
		m_x = x;
	}

	public double getY() {
		return m_y;
	}

	public void setY(double y) {
		m_y = y;
	}

	public double getZ() {
		return m_z;
	}

	public void setZ(double z) {
		m_z = z;
	}

	public double getVx() {
		return m_vx;
	}

	public void setVx(double vx) {
		m_vx = vx;
	}

	public double getVy() {
		return m_vy;
	}

	public void setVy(double vy) {
		m_vy = vy;
	}

	public double getVz() {
		return m_vz;
	}

	public void setVz(double vz) {
		m_vz = vz;
	}

	public double getRightAcensionRadians() {
		return m_rightAcensionRadians;
	}

	public void setRightAcensionRadians(double rightAcensionRadians) {
		m_rightAcensionRadians = rightAcensionRadians;
	}

	public double getDeclinationRadians() {
		return m_declinationRadians;
	}

	public void setDeclinationRadians(double declinationRadians) {
		m_declinationRadians = declinationRadians;
	}

	public double getProperMotionRightAscensionRadians() {
		return m_properMotionRightAscensionRadians;
	}

	public void setProperMotionRightAscensionRadians(double properMotionRightAscensionRadians) {
		m_properMotionRightAscensionRadians = properMotionRightAscensionRadians;
	}

	public double getProperMotionDeclinationRadians() {
		return m_properMotionDeclinationRadians;
	}

	public void setProperMotionDeclinationRadians(double properMotionDeclinationRadians) {
		m_properMotionDeclinationRadians = properMotionDeclinationRadians;
	}

	public String getBayerId() {
		return m_bayerId;
	}

	public void setBayerId(String bayerId) {
		m_bayerId = bayerId;
	}

	public String getFlamsteed() {
		return m_flamsteed;
	}

	public void setFlamsteed(String flamsteed) {
		m_flamsteed = flamsteed;
	}

	public String getConstellation() {
		return m_constellation;
	}

	public void setConstellation(String constellation) {
		m_constellation = constellation;
	}

	public String getCompanionStarId() {
		return m_companionStarId;
	}

	public void setCompanionStarId(String companionStarId) {
		m_companionStarId = companionStarId;
	}

	public String getPrimaryStarId() {
		return m_primaryStarId;
	}

	public void setPrimaryStarId(String primaryStarId) {
		m_primaryStarId = primaryStarId;
	}

	public String getMultipleStarId() {
		return m_multipleStarId;
	}

	public void setMultipleStarId(String multipleStarId) {
		m_multipleStarId = multipleStarId;
	}

	public double getLuminosity() {
		return m_luminosity;
	}

	public void setLuminosity(double luminosity) {
		m_luminosity = luminosity;
	}

	public String getVariableStarDesignation() {
		return m_variableStarDesignation;
	}

	public void setVariableStarDesignation(String variableStarDesignation) {
		m_variableStarDesignation = variableStarDesignation;
	}

	public Double getVariableMinimum() {
		return m_variableMinimum;
	}

	public void setVariableMinimum(Double variableMinimum) {
		m_variableMinimum = variableMinimum;
	}

	public Double getVariableMaximum() {
		return m_variableMaximum;
	}

	public void setVariableMaximum(Double variableMaximum) {
		m_variableMaximum = variableMaximum;
	}
}
