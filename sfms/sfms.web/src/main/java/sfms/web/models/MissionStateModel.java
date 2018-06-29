package sfms.web.models;

import java.time.ZonedDateTime;

public class MissionStateModel {

	private String m_key;
	private ZonedDateTime m_dateTime;
	private Long m_objectiveIndex;
	private ZonedDateTime m_startDateTime;
	private ZonedDateTime m_endDateTime;

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

	public Long getObjectiveIndex() {
		return m_objectiveIndex;
	}

	public void setObjectiveIndex(Long objectiveIndex) {
		m_objectiveIndex = objectiveIndex;
	}

	public ZonedDateTime getStartDateTime() {
		return m_startDateTime;
	}

	public void setStartDateTime(ZonedDateTime startDateTime) {
		m_startDateTime = startDateTime;
	}

	public ZonedDateTime getEndDateTime() {
		return m_endDateTime;
	}

	public void setEndDateTime(ZonedDateTime endDateTime) {
		m_endDateTime = endDateTime;
	}
}
