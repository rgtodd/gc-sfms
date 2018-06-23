package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.schemas.DbMissionStatusValues;
import sfms.simulator.json.MissionDefinition;

public abstract class ActorBase implements Actor {

	private Datastore m_datastore;
	private Entity m_dbActor;

	protected ActorBase(Datastore datastore, Entity dbActor) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (dbActor == null) {
			throw new IllegalArgumentException("Argument dbActor is null.");
		}

		m_datastore = datastore;
		m_dbActor = dbActor;
	}

	@Override
	public Key getKey() {
		return m_dbActor.getKey();
	}

	@Override
	public Mission getMission() {

		Mission mission = Mission.getCurrentMission(getDatastore(), getActorKind(), getActorId());

		return mission;
	}

	@Override
	public void assignMission(Instant now, MissionDefinition mission) throws Exception {

		Mission actorMission = new Mission(getActorKind(), getActorId(), now);

		actorMission.setMissionDefinition(mission);
		actorMission.setStatus(DbMissionStatusValues.ACTIVE);
		actorMission.setStartTimestamp(now);

		actorMission.save(m_datastore);
	}

	@Override
	public abstract void updateState(Instant now) throws Exception;

	@Override
	public abstract void initialize(Instant now, boolean reset) throws Exception;

	protected Datastore getDatastore() {
		return m_datastore;
	}

	protected String getActorKind() {
		return getKey().getKind();
	}

	protected long getActorId() {
		return getKey().getId();
	}
}
