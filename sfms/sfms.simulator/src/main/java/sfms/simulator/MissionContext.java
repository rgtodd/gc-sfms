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

	public ObjectiveDefinition getObjective() {
		MissionDefinition mission = getMissionDefinition();
		if (mission == null) {
			LOGGER.info("    getObjective: Mission does not exist.");
			return null;
		}

		Long objectiveIndex = getObjectiveIndex();
		if (objectiveIndex == null) {
			LOGGER.info("    getObjective: ObjectiveIndex does not exist.");
			return null;
		}

		return mission.getObjectives().get((int) (long) objectiveIndex);
	}

	public void createMission(Instant now) {

		MissionGenerator generator = new MissionGenerator();
		MissionDefinition missionDefinition = generator.createMission(m_actorKind);

		Mission mission = new Mission(m_actorKind, m_actorId, now);
		mission.setMissionDefinition(missionDefinition);
		mission.setStatus(DbMissionStatusValues.ACTIVE);
		mission.save(m_datastore);
		LOGGER.log(Level.INFO, "  Creating mission: {0} / {1} / {2}",
				new Object[] {
						mission.getActorKind(),
						mission.getActorId(),
						mission.getSerialInstant() });

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

		invalidateMission();
	}

	public boolean markCurrentObjectiveComplete(Instant now) {

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

			invalidateMissionState();

			return false;

		} else {

			// Update current mission.
			//
			Mission mission = getMission();
			mission.setStatus(DbMissionStatusValues.COMPLETE);
			mission.save(m_datastore);
			LOGGER.log(Level.INFO, "  Mark mission complete: {0} / {1} / {2}",
					new Object[] {
							mission.getActorKind(),
							mission.getActorId(),
							mission.getSerialInstant() });

			createMission(now);

			return true;
		}
	}

	private Mission getMission() {
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

	private MissionState getMissionState() {
		if (m_missionState == null) {
			Mission mission = getMission();
			if (mission != null) {
				m_missionState = MissionState.getCurrentMission(m_datastore, m_actorKind,
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

	private MissionDefinition getMissionDefinition() {
		Mission mission = getMission();
		if (mission == null) {
			LOGGER.info("    getMissionDefinition: Mission does not exist.");
			return null;
		}

		return mission.getMissionDefinition();
	}

	private Long getObjectiveIndex() {
		MissionState missionState = getMissionState();
		if (missionState == null) {
			LOGGER.info("    getObjectiveIndex: ActorMissionState does not exist.");
			return null;
		}

		return missionState.getObjectiveIndex();
	}

	private void invalidateMission() {
		m_mission = null;
		m_missionState = null;
	}

	private void invalidateMissionState() {
		m_missionState = null;
	}
}
