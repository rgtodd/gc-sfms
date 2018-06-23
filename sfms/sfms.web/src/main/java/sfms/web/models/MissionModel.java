package sfms.web.models;

import java.time.ZonedDateTime;
import java.util.List;

public class MissionModel {

	private String m_key;
	private String m_status;
	private ZonedDateTime m_startDateTime;
	private ZonedDateTime m_endDateTime;
	private List<MissionObjectiveModel> m_objectives;
	private List<MissionStateModel> m_missionStates;

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

	public List<MissionObjectiveModel> getObjectives() {
		return m_objectives;
	}

	public void setObjectives(List<MissionObjectiveModel> objectives) {
		m_objectives = objectives;
	}

	public List<MissionStateModel> getMissionStates() {
		return m_missionStates;
	}

	public void setMissionStates(List<MissionStateModel> missionStates) {
		m_missionStates = missionStates;
	}

}
