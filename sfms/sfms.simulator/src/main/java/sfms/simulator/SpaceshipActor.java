package sfms.simulator;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.DbEntityWrapper;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbStarField;
import sfms.simulator.json.ObjectiveDefinition;
import sfms.simulator.json.TravelObjectiveDefinition;
import sfms.simulator.json.WaitObjectiveDefinition;

public class SpaceshipActor extends ActorBase implements Actor {

	private static final Logger LOGGER = Logger.getLogger(SpaceshipActor.class.getName());

	private static final Random RANDOM = new Random();

	public SpaceshipActor(Datastore datastore, Entity dbActor) {
		super(datastore, dbActor);

		if (!dbActor.getKey().getKind().equals(DbEntity.Spaceship.getKind())) {
			throw new IllegalArgumentException("dbActor is not spaceship.");
		}
	}

	@Override
	public void initialize(Instant now, boolean reset) throws Exception {

		//
		// Check if a state entity exists..
		//
		if (reset) {
			SpaceshipActorState.reset(getDatastore(), getActorId());
		} else {
			if (SpaceshipActorState.exists(getDatastore(), getActorId())) {
				return;
			}
		}

		//
		// Create new state entity.
		//

		SpaceshipActorState state = SpaceshipActorState.generateRandomState(getActorId(),
				now);

		String key = state.save(getDatastore());

		LOGGER.info("  Created initial state for spaceship.  Key = " + key);
	}

	@Override
	public void updateState(Instant now) throws Exception {

		SpaceshipActorState currentState = SpaceshipActorState.getCurrentState(getDatastore(), getActorId());
		if (currentState == null) {
			throw new Exception("Current state does not exist.");
		}

		Duration duration = Duration.between(currentState.getTimestamp(), now);

		SpaceshipActorState updatedState = currentState.apply(duration);

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

		updatedState.save(getDatastore());
	}

	private void updateStateForObjective(SpaceshipActorState state, ObjectiveDefinition objective)
			throws Exception {
		if (objective instanceof WaitObjectiveDefinition) {
			state.setSpeed(0.0);
		} else if (objective instanceof TravelObjectiveDefinition) {
			TravelObjectiveDefinition travelObjective = (TravelObjectiveDefinition) objective;
			String destinationKeyKind = travelObjective.getDestinationKeyKind();
			String destinationKeyValue = travelObjective.getDestinationKeyValue();
			Key dbDestinationKey = getDatastore().newKeyFactory()
					.setKind(destinationKeyKind)
					.newKey(destinationKeyValue);
			if (state.getDestinationKey() == null
					|| !state.getDestinationKey().equals(dbDestinationKey)) {
				if (destinationKeyKind.equals(DbEntity.Star.getKind())) {
					DbEntityWrapper dbStar = DbEntityWrapper.wrap(getDatastore().get(dbDestinationKey));
					state.setSpeed(RANDOM.nextDouble() * 100 + 100);
					state.setDestinationX(dbStar.getDouble(DbStarField.X));
					state.setDestinationY(dbStar.getDouble(DbStarField.Y));
					state.setDestinationZ(dbStar.getDouble(DbStarField.Z));
					state.setDestinationKey(dbDestinationKey);
				} else {
					throw new Exception("Unknown destination kind " + destinationKeyKind);
				}
			}
		} else {
			throw new Exception("Unknown objective " + objective);
		}
	}

	private boolean isObjectiveComplete(ObjectiveDefinition objective, SpaceshipActorState state) {

		if (objective instanceof WaitObjectiveDefinition) {
			return isWaitObjectiveComplete((WaitObjectiveDefinition) objective, state);
		}

		if (objective instanceof TravelObjectiveDefinition) {
			return isTravelObjectiveComplete((TravelObjectiveDefinition) objective, state);
		}

		throw new IllegalArgumentException("Illegal objective type " + objective.getClass().getName());
	}

	private boolean isWaitObjectiveComplete(WaitObjectiveDefinition waitObjective, SpaceshipActorState state) {
		Instant locationArrival = state.getLocationArrival();
		if (locationArrival == null) {
			return false;
		}

		Duration duration = Duration.between(locationArrival, state.getTimestamp());
		return duration.compareTo(waitObjective.getWaitDuration()) >= 0;

	}

	private boolean isTravelObjectiveComplete(TravelObjectiveDefinition travelObjective, SpaceshipActorState state) {
		Key locationKey = state.getLocationKey();
		if (locationKey == null) {
			return false;
		}

		return locationKey.getName().equals(travelObjective.getDestinationKeyValue());
	}

}
