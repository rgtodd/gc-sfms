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

		MissionContext context = new MissionContext(getDatastore(), getActorKind(), getActorId());
		if (context.hasMissionState()) {
			ObjectiveDefinition objective = context.getObjective();
			if (isObjectiveComplete(objective, updatedState)) {
				context.markCurrentObjectiveComplete(now);
			}
		}

		if (!context.hasMission() || context.isMissionComplete()) {
			context.createMission(now);
		}

		if (context.hasMissionState()) {
			ObjectiveDefinition objective = context.getObjective();
			updateStateForObjective(updatedState, objective);
		}

		if (!equals(updatedState, currentState)) {
			updatedState.save(getDatastore());
		}
	}

	private void updateStateForObjective(CrewMemberActorState state, ObjectiveDefinition objective)
			throws Exception {
		if (objective instanceof WaitObjectiveDefinition) {
			// No action required.
		} else if (objective instanceof TravelObjectiveDefinition) {
			TravelObjectiveDefinition travelObjective = (TravelObjectiveDefinition) objective;
			String destinationKeyKind = travelObjective.getDestinationKeyKind();
			String destinationKeyValue = travelObjective.getDestinationKeyValue();
			Key dbDestinationKey = getDatastore().newKeyFactory()
					.setKind(destinationKeyKind)
					.newKey(destinationKeyValue);
			if (state.getDestinationKey() == null
					|| !state.getDestinationKey().equals(dbDestinationKey)) {
				state.setDestinationKey(dbDestinationKey);
			}
		} else {
			throw new Exception("Unknown objective " + objective);
		}
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

	private boolean equals(CrewMemberActorState lhs, CrewMemberActorState rhs) {
		return equals(lhs.getLocationKey(), rhs.getLocationKey())
				&& equals(lhs.getLocationArrival(), rhs.getLocationArrival())
				&& equals(lhs.getDestinationKey(), rhs.getDestinationKey());
	}

	private boolean equals(Key lhs, Key rhs) {
		if (lhs == null && rhs == null) {
			return true;
		}

		if (lhs == null || rhs == null) {
			return false;
		}

		return lhs.equals(rhs);
	}

	private boolean equals(Instant lhs, Instant rhs) {
		if (lhs == null && rhs == null) {
			return true;
		}

		if (lhs == null || rhs == null) {
			return false;
		}

		return lhs.equals(rhs);
	}
}
