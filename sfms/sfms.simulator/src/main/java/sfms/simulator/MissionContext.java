package sfms.simulator;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;

import sfms.db.schemas.DbMissionStatusValues;
import sfms.simulator.json.MissionDefinition;
import sfms.simulator.json.ObjectiveDefinition;

public class MissionContext {

	private static final Logger LOGGER = Logger.getLogger(MissionContext.class.getName());

	private Datastore m_datastore;
	private String m_actorKind;
	private long m_actorId;

	private Mission m_mission;
	private MissionState m_missionState;

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

	public Mission getMission() {
		Mission mission = getMissionCore();
		if (mission == null) {
			throw new IllegalStateException("Mission does not exist.");
		}
		return mission;
	}

	public boolean hasMission() {
		return getMissionCore() != null;
	}

	public boolean isMissionComplete() {
		return getMission().getStatus().equals(DbMissionStatusValues.COMPLETE);
	}

	public void invalidateMission() {
		m_mission = null;
		m_missionState = null;
	}

	public MissionState getMissionState() {
		MissionState missionState = getMissionStateCore();
		if (missionState == null) {
			throw new IllegalStateException("Mission state does not exist.");
		}
		return missionState;
	}

	public boolean hasMissionState() {
		return getMissionStateCore() != null;
	}

	public void invalidateMissionState() {
		m_missionState = null;
	}

	public MissionDefinition getMissionDefinition() {
		Mission mission = getMission();
		return mission.getMissionDefinition();
	}

	public long getObjectiveIndex() {
		MissionState missionState = getMissionState();
		return missionState.getObjectiveIndex();
	}

	public ObjectiveDefinition getObjective() {
		MissionDefinition mission = getMissionDefinition();
		long objectiveIndex = getObjectiveIndex();
		return mission.getObjectives().get((int) objectiveIndex);
	}

	public void createMission(Instant now) throws Exception {

		MissionGenerator generator = new MissionGenerator();
		MissionDefinition missionDefinition = generator.createMission(m_actorKind);

		Mission mission = new Mission(m_actorKind, m_actorId, now);
		mission.setMissionDefinition(missionDefinition);
		mission.setStatus(DbMissionStatusValues.ACTIVE);
		mission.setStartTimestamp(now);
		mission.save(m_datastore);

		LOGGER.log(Level.INFO, "  Creating mission: {0} / {1} / {2}",
				new Object[] {
						mission.getActorKind(),
						mission.getActorId(),
						mission.getSerialInstant() });

		m_mission = mission;

		MissionState missionState = new MissionState(m_actorKind, m_actorId,
				mission.getSerialInstant(), now);
		missionState.setTimestamp(now);
		missionState.setStartTimestamp(now);
		missionState.setObjectiveIndex(0L);
		missionState.save(m_datastore);

		LOGGER.log(Level.INFO, "  Create mission state for objective {4}: {0} / {1} / {2} / {3}",
				new Object[] {
						missionState.getActorKind(),
						missionState.getActorId(),
						missionState.getMissionSerialInstant(),
						missionState.getStateSerialInstant(),
						missionState.getObjectiveIndex() });

		m_missionState = missionState;
	}

	public void markCurrentObjectiveComplete(Instant now) throws Exception {

		// Update current mission state.
		//
		MissionState missionState = getMissionState();
		missionState.setTimestamp(now);
		missionState.setEndTimestamp(now);
		missionState.save(m_datastore);

		LOGGER.log(Level.INFO, "  Mark mission state complete for objective {4}: {0} / {1} / {2} / {3}",
				new Object[] {
						missionState.getActorKind(),
						missionState.getActorId(),
						missionState.getMissionSerialInstant(),
						missionState.getStateSerialInstant(),
						missionState.getObjectiveIndex() });

		Long nextObjectiveIndex = missionState.getObjectiveIndex() + 1;
		if (nextObjectiveIndex < getMissionDefinition().getObjectives().size()) {

			// Create new mission state for next objective.
			//
			missionState = new MissionState(
					m_actorKind,
					m_actorId,
					getMission().getSerialInstant(),
					now);
			missionState.setTimestamp(now);
			missionState.setObjectiveIndex(nextObjectiveIndex);
			missionState.setStartTimestamp(now);
			missionState.save(m_datastore);

			LOGGER.log(Level.INFO, "  Create mission state for objective {4}: {0} / {1} / {2} / {3}",
					new Object[] {
							missionState.getActorKind(),
							missionState.getActorId(),
							missionState.getMissionSerialInstant(),
							missionState.getStateSerialInstant(),
							missionState.getObjectiveIndex() });

			m_missionState = missionState;

		} else {

			// Update current mission.
			//
			Mission mission = getMission();
			mission.setStatus(DbMissionStatusValues.COMPLETE);
			mission.setEndTimestamp(now);
			mission.save(m_datastore);

			LOGGER.log(Level.INFO, "  Mark mission complete: {0} / {1} / {2}",
					new Object[] {
							mission.getActorKind(),
							mission.getActorId(),
							mission.getSerialInstant() });
		}
	}

	private Mission getMissionCore() {
		if (m_mission == null) {
			m_mission = Mission.getCurrentMission(m_datastore, m_actorKind, m_actorId);
			if (m_mission == null) {
				m_mission = Mission.NULL;
			}
		}

		if (m_mission == Mission.NULL) {
			return null;
		}

		return m_mission;
	}

	private MissionState getMissionStateCore() {
		if (m_missionState == null) {
			Mission mission = getMissionCore();
			if (mission != null) {
				m_missionState = MissionState.getCurrentMissionState(m_datastore, m_actorKind,
						m_actorId, mission.getSerialInstant());
				if (m_missionState == null) {
					m_missionState = MissionState.NULL;
				}
			}
		}

		if (m_missionState == MissionState.NULL) {
			return null;
		}

		return m_missionState;
	}
}
