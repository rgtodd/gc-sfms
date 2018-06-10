package sfms.simulator.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class MissionDefinition {

	private static final TypeReference<MissionDefinition> TYPE_REFERENCE = new TypeReference<MissionDefinition>() {
	};

	private List<ObjectiveDefinition> m_objectives;

	public static MissionDefinition fromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			MissionDefinition mission = mapper.readValue(json, TYPE_REFERENCE);
			return mission;
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot deserialize value.", e);
		}
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerFor(MissionDefinition.class);
		try {
			String json = writer.writeValueAsString(this);
			return json;
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Cannot serialize value.", e);
		}
	}

	public List<ObjectiveDefinition> getObjectives() {
		return m_objectives;
	}

	public void setObjectives(List<ObjectiveDefinition> objectives) {
		m_objectives = objectives;
	}
}
