package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;

import sfms.db.schemas.DbEntity;

public class CrewMemberActor extends ActorBase {

	public CrewMemberActor(Datastore datastore, Entity dbCrewMember) {
		super(datastore, dbCrewMember);

		if (!dbCrewMember.getKey().getKind().equals(DbEntity.CrewMember.getKind())) {
			throw new IllegalArgumentException("dbCrewMember is not CrewMember.");
		}
	}

	@Override
	public void initialize(Instant now, boolean reset) throws Exception {
		// TODO: Implement CrewMemberActor::initialize

	}

	@Override
	public void updateState(Instant now) throws Exception {
		// TODO: Implement CrewMemberActor::updateStatus
	}

}
