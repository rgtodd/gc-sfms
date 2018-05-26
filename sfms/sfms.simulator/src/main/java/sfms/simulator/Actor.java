package sfms.simulator;

import java.time.Instant;

import sfms.simulator.json.Mission;

public interface Actor {

	public void assignMission(Mission mission);

	public void updateState(Instant now);

	public ActorKey getKey();

	public void initialize(Instant now, boolean reset);
}
