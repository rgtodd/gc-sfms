package sfms.web.models;

import java.time.ZonedDateTime;

public class CrewMemberStateModel {

	private String m_key;
	private ZonedDateTime m_dateTime;
	private String m_locationKeyKind;
	private String m_locationKeyValue;
	private ZonedDateTime m_locationArrivalDateTime;
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

	public String getLocationKey() {
		return ModelUtility.formatKey(m_locationKeyKind, m_locationKeyValue);
	}

	public String getDestinationKey() {
		return ModelUtility.formatKey(m_destinationKeyKind, m_destinationKeyValue);
	}
}
