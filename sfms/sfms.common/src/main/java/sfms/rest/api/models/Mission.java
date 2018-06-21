package sfms.rest.api.models;

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
	private List<MissionObjective> m_objectives;

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

	public List<MissionObjective> getObjectives() {
		return m_objectives;
	}

	public void setObjectives(List<MissionObjective> objectives) {
		m_objectives = objectives;
	}
}
