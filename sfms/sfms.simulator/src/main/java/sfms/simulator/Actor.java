package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Key;

import sfms.simulator.json.Mission;

public interface Actor {

	public ActorMission getMission();

	public void assignMission(Instant now, Mission mission);

	public void updateState(Instant now);

	public Key getKey();

	public void initialize(Instant now, boolean reset);
}
