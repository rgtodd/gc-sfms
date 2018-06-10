package sfms.simulator;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;

import sfms.db.schemas.DbMissionStatusValues;
import sfms.simulator.json.MissionDefinition;
import sfms.simulator.json.ObjectiveDefinition;

public class MissionContext {

	private final Logger logger = Logger.getLogger(MissionContext.class.getName());

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

	public MissionDefinition getMission() {
		ActorMission actorMission = getActorMission();
		if (actorMission == null) {
			logger.info("    getMission: ActorMission does not exist.");
			return null;
		}

		return actorMission.getMission();
	}

	public Long getObjectiveIndex() {
		ActorMissionState actorMissionState = getActorMissionState();
		if (actorMissionState == null) {
			logger.info("    getObjectiveIndex: ActorMissionState does not exist.");
			return null;
		}

		return actorMissionState.getObjectiveIndex();
	}

	public ObjectiveDefinition getObjective() {
		MissionDefinition mission = getMission();
		if (mission == null) {
			logger.info("    getObjective: Mission does not exist.");
			return null;
		}

		Long objectiveIndex = getObjectiveIndex();
		if (objectiveIndex == null) {
			logger.info("    getObjective: ObjectiveIndex does not exist.");
			return null;
		}

		return mission.getObjectives().get((int) (long) objectiveIndex);
	}

	public void createMission(Instant now) {

		invalidateActorMission();

		m_actorMission = createActorMission(now);
		m_actorMissionState = createActorMissionState(now);
	}

	public boolean markCurrentObjectiveComplete(Instant now) {

		// Update current mission state.
		//
		ActorMissionState actorMissionState = getActorMissionState();
		actorMissionState.setTimestamp(now);
		actorMissionState.setEndTimestamp(now);
		actorMissionState.save(m_datastore);
		logger.log(Level.INFO, "  Mark mission state complete for objective {4}: {0} / {1} / {2} / {3}",
				new Object[] {
						actorMissionState.getActorKind(),
						actorMissionState.getActorId(),
						actorMissionState.getMissionSerialInstant(),
						actorMissionState.getStateSerialInstant(),
						actorMissionState.getObjectiveIndex() });

		Long nextObjectiveIndex = actorMissionState.getObjectiveIndex() + 1;

		if (nextObjectiveIndex < getMission().getObjectives().size()) {

			// Create new mission state for next objective.
			//
			actorMissionState = new ActorMissionState(
					m_actorKind,
					m_actorId,
					getActorMission().getSerialInstant(),
					now);
			actorMissionState.setTimestamp(now);
			actorMissionState.setObjectiveIndex(nextObjectiveIndex);
			actorMissionState.setStartTimestamp(now);
			actorMissionState.save(m_datastore);
			logger.log(Level.INFO, "  Create mission state for objective {4}: {0} / {1} / {2} / {3}",
					new Object[] {
							actorMissionState.getActorKind(),
							actorMissionState.getActorId(),
							actorMissionState.getMissionSerialInstant(),
							actorMissionState.getStateSerialInstant(),
							actorMissionState.getObjectiveIndex() });

			invalidateActorMissionState();

			return false;

		} else {

			// Update current mission.
			//
			ActorMission actorMission = getActorMission();
			actorMission.setStatus(DbMissionStatusValues.COMPLETE);
			actorMission.save(m_datastore);
			logger.log(Level.INFO, "  Mark mission complete: {0} / {1} / {2}",
					new Object[] {
							actorMission.getActorKind(),
							actorMission.getActorId(),
							actorMission.getSerialInstant() });

			invalidateActorMission();

			m_actorMission = createActorMission(now);
			m_actorMissionState = createActorMissionState(now);

			return true;
		}
	}

	private ActorMission createActorMission(Instant now) {

		MissionGenerator generator = new MissionGenerator();
		MissionDefinition mission = generator.createMission(m_actorKind);

		ActorMission actorMission = new ActorMission(m_actorKind, m_actorId, now);
		actorMission.setMission(mission);
		actorMission.setStatus(DbMissionStatusValues.ACTIVE);
		actorMission.save(m_datastore);
		logger.log(Level.INFO, "  Creating mission: {0} / {1} / {2}",
				new Object[] {
						actorMission.getActorKind(),
						actorMission.getActorId(),
						actorMission.getSerialInstant() });

		return actorMission;
	}

	private ActorMissionState createActorMissionState(Instant now) {

		ActorMissionState actorMissionState = new ActorMissionState(m_actorKind, m_actorId,
				getActorMission().getSerialInstant(), now);
		actorMissionState.setTimestamp(now);
		actorMissionState.setStartTimestamp(now);
		actorMissionState.setObjectiveIndex(0L);
		actorMissionState.save(m_datastore);
		logger.log(Level.INFO, "  Create mission state for objective {4}: {0} / {1} / {2} / {3}",
				new Object[] {
						actorMissionState.getActorKind(),
						actorMissionState.getActorId(),
						actorMissionState.getMissionSerialInstant(),
						actorMissionState.getStateSerialInstant(),
						actorMissionState.getObjectiveIndex() });

		return actorMissionState;
	}

	private void invalidateActorMission() {
		m_actorMission = null;
		m_actorMissionState = null;
	}

	private void invalidateActorMissionState() {
		m_actorMissionState = null;
	}
}
