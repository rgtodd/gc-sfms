package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;

import sfms.db.schemas.DbEntity;
import sfms.simulator.json.Mission;

public class CrewMemberActor extends ActorBase implements Actor {

	public CrewMemberActor(Datastore datastore, Entity dbCrewMember) {
		super(datastore, dbCrewMember);

		if (!dbCrewMember.getKey().getKind().equals(DbEntity.CrewMember.getKind())) {
			throw new IllegalArgumentException("dbCrewMember is not CrewMember.");
		}
	}

	@Override
	public void assignMission(Instant now, Mission mission) {
		assignMissionBase(now, mission);
	}

	@Override
	public void updateState(Instant now) {
		// TODO Auto-generated method stub

	}

	@Override
	public ActorKey getActorKey() {
		return getActorKeyBase();
	}

	@Override
	public void initialize(Instant now, boolean reset) {
		// TODO Auto-generated method stub

	}

	@Override
	public Mission getMission() {
		// TODO Auto-generated method stub
		return null;
	}

}
