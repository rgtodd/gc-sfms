package sfms.simulator.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Mission {

	private static final TypeReference<Mission> TYPE_REFERENCE = new TypeReference<Mission>() {
	};

	private List<Objective> m_objectives;

	public static Mission fromJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Mission mission = mapper.readValue(json, TYPE_REFERENCE);
			return mission;
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot deserialize value.", e);
		}
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerFor(Mission.class);
		try {
			String json = writer.writeValueAsString(this);
			return json;
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Cannot serialize value.", e);
		}
	}

	public List<Objective> getObjectives() {
		return m_objectives;
	}

	public void setObjectives(List<Objective> objectives) {
		m_objectives = objectives;
	}
}
