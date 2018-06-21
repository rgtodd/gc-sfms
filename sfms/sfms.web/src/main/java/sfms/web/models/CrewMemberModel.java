package sfms.web.models;

import java.util.List;

public class CrewMemberModel {

	private String m_key;
	private String m_firstName;
	private String m_lastName;
	private List<MissionModel> m_missions;
	private List<MissionStateModel> m_missionStates;
	private List<CrewMemberStateModel> m_states;

	public CrewMemberModel() {
	}

	public String getFirstName() {
		return m_firstName;
	}

	public void setFirstName(String firstName) {
		m_firstName = firstName;
	}

	public String getLastName() {
		return m_lastName;
	}

	public void setLastName(String lastName) {
		m_lastName = lastName;
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public List<MissionModel> getMissions() {
		return m_missions;
	}

	public void setMissions(List<MissionModel> missions) {
		m_missions = missions;
	}

	public List<MissionStateModel> getMissionStates() {
		return m_missionStates;
	}

	public void setMissionStates(List<MissionStateModel> missionStates) {
		m_missionStates = missionStates;
	}

	public List<CrewMemberStateModel> getStates() {
		return m_states;
	}

	public void setStates(List<CrewMemberStateModel> states) {
		m_states = states;
	}

	@Override
	public String toString() {
		return "Key = " + getKey() + ", First Name = " + getFirstName() + ", Last Name = " + getLastName();
	}

}
