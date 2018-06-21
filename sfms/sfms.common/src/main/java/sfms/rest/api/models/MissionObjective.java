package sfms.rest.api.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Mission Objective entity exposed by the Crew Member and Spaceship
 * REST services.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MissionObjective {

	private String m_description;
	private Instant m_startDateTime;
	private Instant m_endDateTime;

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		m_description = description;
	}

	public Instant getStartDateTime() {
		return m_startDateTime;
	}

	public void setStartDateTime(Instant startDateTime) {
		m_startDateTime = startDateTime;
	}

	public Instant getEndDateTime() {
		return m_endDateTime;
	}

	public void setEndDateTime(Instant endDateTime) {
		m_endDateTime = endDateTime;
	}

}
