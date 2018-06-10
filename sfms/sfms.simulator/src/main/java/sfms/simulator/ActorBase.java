package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.schemas.DbMissionStatusValues;
import sfms.simulator.json.MissionDefinition;

public abstract class ActorBase implements Actor {

	private Datastore m_datastore;
	private Entity m_dbEntity;

	protected ActorBase(Datastore datastore, Entity dbEntity) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (dbEntity == null) {
			throw new IllegalArgumentException("Argument dbEntity is null.");
		}

		m_datastore = datastore;
		m_dbEntity = dbEntity;
	}

	@Override
	public Key getKey() {
		return m_dbEntity.getKey();
	}

	@Override
	public ActorMission getMission() {

		ActorMission mission = ActorMission.getCurrentMission(getDatastore(), getActorKind(), getActorId());

		return mission;
	}

	@Override
	public void assignMission(Instant now, MissionDefinition mission) {

		ActorMission actorMission = new ActorMission(getActorKind(), getActorId(), now);

		actorMission.setMission(mission);
		actorMission.setStatus(DbMissionStatusValues.ACTIVE);

		actorMission.save(m_datastore);
	}

	@Override
	public abstract void updateState(Instant now);

	@Override
	public abstract void initialize(Instant now, boolean reset);

	protected Datastore getDatastore() {
		return m_datastore;
	}

	protected Entity getEntity() {
		return m_dbEntity;
	}

	protected String getActorKind() {
		return getKey().getKind();
	}

	protected long getActorId() {
		return getKey().getId();
	}
}
