package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Key;

import sfms.simulator.json.Mission;

public interface Actor {

	public void assignMission(Mission mission);

	public void updateStatus(Instant now);

	public Key getEntityKey();
}
