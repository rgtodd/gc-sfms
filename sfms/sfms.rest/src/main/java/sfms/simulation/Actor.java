package sfms.simulation;

import java.time.Instant;

import com.google.cloud.datastore.Key;

import sfms.simulation.json.Mission;

public interface Actor {

	public void assignMission(Mission mission);

	public void updateStatus(Instant now);

	public Key getEntityKey();
}
