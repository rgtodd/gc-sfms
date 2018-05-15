package sfms.simulation.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = TravelObjective.class, name = "travelObjective"),
		@JsonSubTypes.Type(value = WaitObjective.class, name = "waitObjective")
})
public class Objective {

	private int m_objectiveId;
	private Instant m_startingTime;
	private Instant m_endingTime;

	public int getObjectiveId() {
		return m_objectiveId;
	}

	public void setObjectiveId(int objectiveId) {
		m_objectiveId = objectiveId;
	}

	public Instant getStartingTime() {
		return m_startingTime;
	}

	public void setStartingTime(Instant startingTime) {
		m_startingTime = startingTime;
	}

	public Instant getEndingTime() {
		return m_endingTime;
	}

	public void setEndingTime(Instant endingTime) {
		m_endingTime = endingTime;
	}
}
