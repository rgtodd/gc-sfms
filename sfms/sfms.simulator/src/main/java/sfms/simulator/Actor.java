package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Key;

import sfms.simulator.json.MissionDefinition;

public interface Actor {

	public ActorMission getMission();

	public void assignMission(Instant now, MissionDefinition mission);

	public void updateState(Instant now);

	public Key getKey();

	public void initialize(Instant now, boolean reset);
}
