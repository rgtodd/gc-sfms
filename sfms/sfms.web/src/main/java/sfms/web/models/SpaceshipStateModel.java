package sfms.web.models;

import java.time.ZonedDateTime;

import org.springframework.format.annotation.NumberFormat;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceshipStateModel {

	private String m_key;
	private ZonedDateTime m_dateTime;
	private Double m_locationX;
	private Double m_locationY;
	private Double m_locationZ;
	private String m_locationKeyKind;
	private String m_locationKeyValue;
	private ZonedDateTime m_locationArrivalDateTime;
	private Double m_speed;
	private Double m_destinationX;
	private Double m_destinationY;
	private Double m_destinationZ;
	private String m_destinationKeyKind;
	private String m_destinationKeyValue;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public ZonedDateTime getDateTime() {
		return m_dateTime;
	}

	public void setDateTime(ZonedDateTime dateTime) {
		m_dateTime = dateTime;
	}

	public Double getLocationX() {
		return m_locationX;
	}

	public void setLocationX(Double locationX) {
		m_locationX = locationX;
	}

	public Double getLocationY() {
		return m_locationY;
	}

	public void setLocationY(Double locationY) {
		m_locationY = locationY;
	}

	public Double getLocationZ() {
		return m_locationZ;
	}

	public void setLocationZ(Double locationZ) {
		m_locationZ = locationZ;
	}

	public String getLocationKeyKind() {
		return m_locationKeyKind;
	}

	public void setLocationKeyKind(String locationKeyKind) {
		m_locationKeyKind = locationKeyKind;
	}

	public String getLocationKeyValue() {
		return m_locationKeyValue;
	}

	public void setLocationKeyValue(String locationKeyValue) {
		m_locationKeyValue = locationKeyValue;
	}

	public ZonedDateTime getLocationArrivalDateTime() {
		return m_locationArrivalDateTime;
	}

	public void setLocationArrivalDateTime(ZonedDateTime locationArrivalDateTime) {
		m_locationArrivalDateTime = locationArrivalDateTime;
	}

	@NumberFormat(pattern = "#.00")
	public Double getSpeed() {
		return m_speed;
	}

	public void setSpeed(Double speed) {
		m_speed = speed;
	}

	public Double getDestinationX() {
		return m_destinationX;
	}

	public void setDestinationX(Double destinationX) {
		m_destinationX = destinationX;
	}

	public Double getDestinationY() {
		return m_destinationY;
	}

	public void setDestinationY(Double destinationY) {
		m_destinationY = destinationY;
	}

	public Double getDestinationZ() {
		return m_destinationZ;
	}

	public void setDestinationZ(Double destinationZ) {
		m_destinationZ = destinationZ;
	}

	public String getDestinationKeyKind() {
		return m_destinationKeyKind;
	}

	public void setDestinationKeyKind(String destinationKeyKind) {
		m_destinationKeyKind = destinationKeyKind;
	}

	public String getDestinationKeyValue() {
		return m_destinationKeyValue;
	}

	public void setDestinationKeyValue(String destinationKeyValue) {
		m_destinationKeyValue = destinationKeyValue;
	}

	public String getLocationXYZ() {
		return ModelUtility.formatCoordinates(m_locationX, m_locationY, m_locationZ);
	}

	public String getLocationKey() {
		return ModelUtility.formatKey(m_locationKeyKind, m_locationKeyValue);
	}

	public String getDestinationXYZ() {
		return ModelUtility.formatCoordinates(m_destinationX, m_destinationY, m_destinationZ);
	}

	public String getDestinationKey() {
		return ModelUtility.formatKey(m_destinationKeyKind, m_destinationKeyValue);
	}
}
