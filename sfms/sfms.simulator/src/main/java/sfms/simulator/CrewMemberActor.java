package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Entity;

import sfms.db.schemas.DbEntity;
import sfms.simulator.json.Mission;

public class CrewMemberActor implements Actor {

	private ActorKey m_key;
	@SuppressWarnings("unused")
	private Entity m_dbCrewMember;

	public CrewMemberActor(Entity dbCrewMember) {
		if (dbCrewMember == null) {
			throw new IllegalArgumentException("dbCrewMember is null.");
		}
		if (!dbCrewMember.getKey().getKind().equals(DbEntity.CrewMember.getKind())) {
			throw new IllegalArgumentException("dbCrewMember is not CrewMember.");
		}

		m_key = new ActorKey(dbCrewMember.getKey());
		m_dbCrewMember = dbCrewMember;
	}

	@Override
	public void assignMission(Mission mission) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateState(Instant now) {
		// TODO Auto-generated method stub

	}

	@Override
	public ActorKey getKey() {
		return m_key;
	}

	@Override
	public void initialize(Instant now, boolean reset) {
		// TODO Auto-generated method stub

	}

}
