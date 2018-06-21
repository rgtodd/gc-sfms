package sfms.rest.api.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Crew Member entity exposed by the Crew Member REST service.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrewMember {

	private String m_key;
	private String m_firstName;
	private String m_lastName;
	private List<Mission> m_missions;
	private List<MissionState> m_missionStates;
	private List<CrewMemberState> m_states;

	public CrewMember() {
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
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

	public List<Mission> getMissions() {
		return m_missions;
	}

	public void setMissions(List<Mission> missions) {
		m_missions = missions;
	}

	public List<MissionState> getMissionStates() {
		return m_missionStates;
	}

	public void setMissionStates(List<MissionState> missionStates) {
		m_missionStates = missionStates;
	}

	public List<CrewMemberState> getStates() {
		return m_states;
	}

	public void setStates(List<CrewMemberState> states) {
		m_states = states;
	}
}
