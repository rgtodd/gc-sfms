package sfms.rest.api.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Spaceship entity exposed by the Spaceship REST service.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Spaceship {

	private String m_key;
	private String m_name;
	private List<Mission> m_missions;
	private List<MissionState> m_missionStates;
	private List<SpaceshipState> m_states;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
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

	public List<SpaceshipState> getStates() {
		return m_states;
	}

	public void setStates(List<SpaceshipState> states) {
		m_states = states;
	}

	@Override
	public String toString() {
		return "Id = " + getKey() + ", Name = " + getName();
	}
}
