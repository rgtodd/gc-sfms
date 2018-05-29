package sfms.simulator;

import java.time.Instant;

import sfms.simulator.json.Mission;

public interface Actor {
	
	public ActorMission getMission();

	public void assignMission(Instant now, Mission mission);

	public void updateState(Instant now);

	public ActorKey getActorKey();

	public void initialize(Instant now, boolean reset);
}
