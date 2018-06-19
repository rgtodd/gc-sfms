package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Key;

import sfms.simulator.json.MissionDefinition;

public interface Actor {

	public Mission getMission();

	public void assignMission(Instant now, MissionDefinition mission) throws Exception;

	public void updateState(Instant now) throws Exception;

	public Key getKey();

	public void initialize(Instant now, boolean reset) throws Exception;
}
