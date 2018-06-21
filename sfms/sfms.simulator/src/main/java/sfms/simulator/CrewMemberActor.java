package sfms.simulator;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.schemas.DbEntity;
import sfms.simulator.json.ObjectiveDefinition;
import sfms.simulator.json.TravelObjectiveDefinition;
import sfms.simulator.json.WaitObjectiveDefinition;

public class CrewMemberActor extends ActorBase {

	private static final Logger LOGGER = Logger.getLogger(CrewMemberActor.class.getName());

	public CrewMemberActor(Datastore datastore, Entity dbActor) {
		super(datastore, dbActor);

		if (!dbActor.getKey().getKind().equals(DbEntity.CrewMember.getKind())) {
			throw new IllegalArgumentException("dbActor is not crew member.");
		}
	}

	@Override
	public void initialize(Instant now, boolean reset) throws Exception {

		//
		// Check if a state entity exists..
		//
		if (reset) {
			CrewMemberActorState.reset(getDatastore(), getActorId());
		} else {
			if (CrewMemberActorState.exists(getDatastore(), getActorId())) {
				return;
			}
		}

		//
		// Create new state entity.
		//

		CrewMemberActorState state = CrewMemberActorState.generateRandomState(getActorId(),
				now);

		String key = state.save(getDatastore());

		LOGGER.info("  Created initial state for crew member.  Key = " + key);
	}

	@Override
	public void updateState(Instant now) throws Exception {

		CrewMemberActorState currentState = CrewMemberActorState.getCurrentState(getDatastore(), getActorId());
		if (currentState == null) {
			throw new Exception("Current state does not exist.");
		}

		Duration duration = Duration.between(currentState.getTimestamp(), now);

		CrewMemberActorState updatedState = currentState.apply(duration);

		MissionContext missionContext = new MissionContext(getDatastore(), getActorKind(), getActorId());

		ObjectiveDefinition objective = missionContext.getObjective();
		if (objective == null) {
			missionContext.createMission(now);
		} else if (isObjectiveComplete(objective, updatedState)) {
			missionContext.markCurrentObjectiveComplete(now);
		}

		objective = missionContext.getObjective();
		if (objective == null) {
			throw new Exception("Objective does not exist.");
		}

		if (objective instanceof WaitObjectiveDefinition) {
			// No action required.
		} else if (objective instanceof TravelObjectiveDefinition) {
			TravelObjectiveDefinition travelObjective = (TravelObjectiveDefinition) objective;
			String destinationKeyKind = travelObjective.getDestinationKeyKind();
			String destinationKeyValue = travelObjective.getDestinationKeyValue();
			Key dbDestinationKey = getDatastore().newKeyFactory()
					.setKind(destinationKeyKind)
					.newKey(destinationKeyValue);
			if (updatedState.getDestinationKey() == null
					|| !updatedState.getDestinationKey().equals(dbDestinationKey)) {
				updatedState.setDestinationKey(dbDestinationKey);
			}
		} else {
			throw new Exception("Unknown objective " + objective);
		}

		updatedState.save(getDatastore());
	}

	private boolean isObjectiveComplete(ObjectiveDefinition objective, CrewMemberActorState state) {

		if (objective instanceof WaitObjectiveDefinition) {
			return isWaitObjectiveComplete((WaitObjectiveDefinition) objective, state);
		}

		if (objective instanceof TravelObjectiveDefinition) {
			return isTravelObjectiveComplete((TravelObjectiveDefinition) objective, state);
		}

		throw new IllegalArgumentException("Illegal objective type " + objective.getClass().getName());
	}

	private boolean isWaitObjectiveComplete(WaitObjectiveDefinition waitObjective, CrewMemberActorState state) {
		Instant locationArrival = state.getLocationArrival();
		if (locationArrival == null) {
			return false;
		}

		Duration duration = Duration.between(locationArrival, state.getTimestamp());
		return duration.compareTo(waitObjective.getWaitDuration()) >= 0;

	}

	private boolean isTravelObjectiveComplete(TravelObjectiveDefinition travelObjective, CrewMemberActorState state) {
		Key locationKey = state.getLocationKey();
		if (locationKey == null) {
			return false;
		}

		return locationKey.getName().equals(travelObjective.getDestinationKeyValue());
	}
}
