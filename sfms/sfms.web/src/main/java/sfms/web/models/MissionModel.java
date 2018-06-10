package sfms.web.models;

import java.util.List;

public class MissionModel {

	private String m_key;
	private String m_status;
	private List<MissionObjectiveModel> m_objectives;

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

	public List<MissionObjectiveModel> getObjectives() {
		return m_objectives;
	}

	public void setObjectives(List<MissionObjectiveModel> objectives) {
		m_objectives = objectives;
	}

}
