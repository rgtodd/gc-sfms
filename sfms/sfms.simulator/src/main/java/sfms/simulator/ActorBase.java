package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;

import sfms.simulator.json.Mission;

public abstract class ActorBase implements Actor {

	private Datastore m_datastore;
	private Entity m_dbEntity;
	private ActorKey m_actorKey;

	protected ActorBase(Datastore datastore, Entity dbEntity) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (dbEntity == null) {
			throw new IllegalArgumentException("Argument dbEntity is null.");
		}

		m_datastore = datastore;
		m_dbEntity = dbEntity;
		m_actorKey = new ActorKey(dbEntity.getKey());
	}

	@Override
	public ActorKey getActorKey() {
		return m_actorKey;
	}

	@Override
	public ActorMission getMission() {

		ActorMission mission = ActorMission.getCurrentMission(getDatastore(), getActorKind(), getActorId());

		return mission;
	}

	@Override
	public void assignMission(Instant now, Mission mission) {

		ActorMission actorMission = new ActorMission(getActorKind(), getActorId(), now);

		actorMission.setMission(mission);

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
		return getActorKey().getKey().getKind();
	}

	protected long getActorId() {
		return getActorKey().getKey().getId();
	}
}
