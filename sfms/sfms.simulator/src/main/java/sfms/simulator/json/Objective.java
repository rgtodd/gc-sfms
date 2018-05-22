package sfms.simulator.json;

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

	public int getObjectiveId() {
		return m_objectiveId;
	}

	public void setObjectiveId(int objectiveId) {
		m_objectiveId = objectiveId;

	}
}
