package sfms.rest.api.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Mission State entity exposed by the Crew Member and Spaceship REST
 * services.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MissionState {

	private String m_key;
	private Instant m_timestamp;
	private Long m_objectiveIndex;
	private Instant m_startTimestamp;
	private Instant m_endTimestamp;

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

	public Long getObjectiveIndex() {
		return m_objectiveIndex;
	}

	public void setObjectiveIndex(Long objectiveIndex) {
		m_objectiveIndex = objectiveIndex;
	}

	public Instant getStartTimestamp() {
		return m_startTimestamp;
	}

	public void setStartTimestamp(Instant startTimestamp) {
		m_startTimestamp = startTimestamp;
	}

	public Instant getEndTimestamp() {
		return m_endTimestamp;
	}

	public void setEndTimestamp(Instant endTimestamp) {
		m_endTimestamp = endTimestamp;
	}
}
