package sfms.simulator;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;

import sfms.db.schemas.DbMissionStatusValues;
import sfms.simulator.json.Mission;
import sfms.simulator.json.Objective;

public class MissionContext {

	private Datastore m_datastore;
	private String m_actorKind;
	private long m_actorId;

	private ActorMission m_actorMission;
	private ActorMissionState m_actorMissionState;

	public MissionContext(Datastore datastore, String actorKind, long actorId) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (actorKind == null) {
			throw new IllegalArgumentException("Argument actorKind is null.");
		}

		m_datastore = datastore;
		m_actorKind = actorKind;
		m_actorId = actorId;
	}

	public ActorMission getActorMission() {
		if (m_actorMission == null) {
			m_actorMission = ActorMission.getCurrentMission(m_datastore, m_actorKind, m_actorId);
			if (m_actorMission == null) {
				m_actorMission = ActorMission.NULL;
			}
		}

		if (m_actorMission == ActorMission.NULL) {
			return null;
		}

		return m_actorMission;
	}

	public void invalidateActorMission() {
		m_actorMission = null;
	}

	public ActorMissionState getActorMissionState() {
		if (m_actorMissionState == null) {
			ActorMission actorMission = getActorMission();
			if (actorMission != null) {
				m_actorMissionState = ActorMissionState.getCurrentMission(m_datastore, m_actorKind,
						m_actorId, actorMission.getSerialInstant());
				if (m_actorMissionState == null) {
					m_actorMissionState = ActorMissionState.NULL;
				}
			}
		}

		if (m_actorMissionState == ActorMissionState.NULL) {
			return null;
		}

		return m_actorMissionState;
	}

	public void invalidateActorMissionState() {
		m_actorMissionState = null;
		invalidateActorMissionState();
	}

	public Mission getMission() {
		ActorMission actorMission = getActorMission();
		if (actorMission == null) {
			return null;
		}

		return actorMission.getMission();
	}

	public Long getObjectiveIndex() {
		ActorMissionState actorMissionState = getActorMissionState();
		if (actorMissionState == null) {
			return null;
		}

		return actorMissionState.getObjectiveIndex();
	}

	public Objective getObjective() {
		Mission mission = getMission();
		if (mission == null) {
			return null;
		}

		Long objectiveIndex = getObjectiveIndex();
		if (objectiveIndex == null) {
			return null;
		}

		return mission.getObjectives().get((int) (long) objectiveIndex);
	}

	public boolean markCurrentObjectiveComplete(Instant now) {

		ActorMissionState actorMissionState = getActorMissionState();
		actorMissionState.setTimestamp(now);
		actorMissionState.setEndTimestamp(now);
		actorMissionState.save(m_datastore);

		Long nextObjectiveIndex = actorMissionState.getObjectiveIndex() + 1;

		if (nextObjectiveIndex < getMission().getObjectives().size()) {
			actorMissionState = new ActorMissionState(
					m_actorKind,
					m_actorId,
					getActorMission().getSerialInstant(),
					now);

			actorMissionState.setTimestamp(now);
			actorMissionState.setObjectiveIndex(nextObjectiveIndex);
			actorMissionState.setStartTimestamp(now);
			actorMissionState.save(m_datastore);

			invalidateActorMissionState();

			return false;

		} else {

			ActorMission actorMission = getActorMission();
			actorMission.setStatus(DbMissionStatusValues.COMPLETE);
			actorMission.save(m_datastore);

			invalidateActorMission();

			return true;
		}
	}
}
