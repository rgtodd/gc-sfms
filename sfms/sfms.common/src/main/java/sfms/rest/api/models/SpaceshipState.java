package sfms.rest.api.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Spaceship State entity exposed by the Spaceship REST service.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceshipState {

	private String m_key;
	private Instant m_timestamp;
	private Double m_locationX;
	private Double m_locationY;
	private Double m_locationZ;
	private String m_locationKeyKind;
	private String m_locationKeyValue;
	private Instant m_locationArrival;
	private Double m_speed;
	private Double m_distance;
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

	public Instant getTimestamp() {
		return m_timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		m_timestamp = timestamp;
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

	public Instant getLocationArrival() {
		return m_locationArrival;
	}

	public void setLocationArrival(Instant locationArrival) {
		m_locationArrival = locationArrival;
	}

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

	public Double getDistance() {
		return m_distance;
	}

	public void setDistance(Double distance) {
		m_distance = distance;
	}

}
