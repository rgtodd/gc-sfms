package sfms.web.models;

import java.util.List;

public class SpaceshipModel {

	private String m_key;
	private String m_name;
	private List<MissionModel> m_missions;
	private List<SpaceshipStateModel> m_states;

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	@Override
	public String toString() {
		return "Key = " + getKey() + ", Name = " + getName();
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

	public List<SpaceshipStateModel> getStates() {
		return m_states;
	}

	public void setStates(List<SpaceshipStateModel> states) {
		m_states = states;
	}
}
