package sfms.rest.api.models;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Mission entity exposed by the Crew Member and Spaceship REST
 * services.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mission {

	private String m_key;
	private String m_status;
	private Instant m_startTimestamp;
	private Instant m_endTimestamp;
	private List<MissionObjective> m_objectives;
	private List<MissionState> m_missionStates;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public String getStatus() {
		return m_status;
	}

	public void setStatus(String status) {
		m_status = status;
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

	public List<MissionObjective> getObjectives() {
		return m_objectives;
	}

	public void setObjectives(List<MissionObjective> objectives) {
		m_objectives = objectives;
	}

	public List<MissionState> getMissionStates() {
		return m_missionStates;
	}

	public void setMissionStates(List<MissionState> missionStates) {
		m_missionStates = missionStates;
	}
}
