package sfms.simulator.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = TravelObjectiveDefinition.class, name = "travelObjective"),
		@JsonSubTypes.Type(value = WaitObjectiveDefinition.class, name = "waitObjective")
})
public class ObjectiveDefinition {

	private int m_objectiveId;

	public int getObjectiveId() {
		return m_objectiveId;
	}

	public void setObjectiveId(int objectiveId) {
		m_objectiveId = objectiveId;

	}
}
