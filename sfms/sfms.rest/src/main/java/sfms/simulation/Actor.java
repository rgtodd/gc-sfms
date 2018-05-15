package sfms.simulation;

import java.time.Instant;

import com.google.cloud.datastore.Key;

import sfms.simulation.json.Mission;

public interface Actor {

	public ActorType getActorType();

	public String getActorId();

	public void assignMission(Mission mission);

	public void updateStatus(Instant now);

	public Key getEntityKey();

}
