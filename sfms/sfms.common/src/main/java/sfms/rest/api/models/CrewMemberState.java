package sfms.rest.api.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Crew Member State entity exposed by the Crew Member REST service.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewMemberState {

	private String m_key;
	private Instant m_timestamp;
	private String m_locationKeyKind;
	private String m_locationKeyValue;
	private Instant m_locationArrival;
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

}
