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
import sfms.simulator.json.Objective;
import sfms.simulator.json.TravelObjective;
import sfms.simulator.json.WaitObjective;

public class SpaceshipActor extends ActorBase implements Actor {

	private final Logger logger = Logger.getLogger(SpaceshipActor.class.getName());

	private static final Random RANDOM = new Random();

	public SpaceshipActor(Datastore datastore, Entity dbEntity) {
		super(datastore, dbEntity);

		if (!dbEntity.getKey().getKind().equals(DbEntity.Spaceship.getKind())) {
			throw new IllegalArgumentException("dbEntity is not spaceship.");
		}
	}

	@Override
	public void initialize(Instant now, boolean reset) {

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

		logger.info("  Created initial state for space ship.  Key = " + key);
	}

	@Override
	public void updateState(Instant now) {

		SpaceshipActorState currentState = SpaceshipActorState.getCurrentState(getDatastore(), getActorId());
		if (currentState == null) {
			return;
		}

		Duration duration = Duration.between(currentState.getTimestamp(), now);

		SpaceshipActorState updatedState = currentState.apply(duration);

		MissionContext missionContext = new MissionContext(getDatastore(), getActorKind(), getActorId());

		boolean updateState;
		Objective objective = missionContext.getObjective();
		if (objective == null) {
			missionContext.createMission(now);
			updateState = true;
		} else if (isObjectiveComplete(objective, updatedState)) {
			missionContext.markCurrentObjectiveComplete(now);
			updateState = true;
		} else {
			updateState = false;
		}

		if (updateState) {
			objective = missionContext.getObjective();
			if (objective instanceof WaitObjective) {
				updatedState.setSpeed(0.0);
			} else if (objective instanceof TravelObjective) {
				TravelObjective travelObjective = (TravelObjective) objective;
				Key dbStarKey = getDatastore().newKeyFactory()
						.setKind(DbEntity.Star.getKind())
						.newKey(travelObjective.getStarKey());
				DbEntityWrapper dbStar = DbEntityWrapper.wrap(getDatastore().get(dbStarKey));
				updatedState.setSpeed(RANDOM.nextDouble() * 100 + 100);
				updatedState.setDestinationX(dbStar.getDouble(DbStarField.X));
				updatedState.setDestinationY(dbStar.getDouble(DbStarField.Y));
				updatedState.setDestinationZ(dbStar.getDouble(DbStarField.Z));
				updatedState.setDestinationKey(dbStarKey);
			}
		}

		updatedState.save(getDatastore());
	}

	private boolean isObjectiveComplete(Objective objective, SpaceshipActorState state) {

		if (objective instanceof WaitObjective) {
			return isWaitObjectiveComplete((WaitObjective) objective, state);
		}

		if (objective instanceof TravelObjective) {
			return isTravelObjectiveComplete((TravelObjective) objective, state);
		}

		throw new IllegalArgumentException("Illegal objective type " + objective.getClass().getName());
	}

	private boolean isWaitObjectiveComplete(WaitObjective waitObjective, SpaceshipActorState state) {
		Instant locationArrival = state.getLocationArrival();
		if (locationArrival == null) {
			return false;
		}

		Duration duration = Duration.between(locationArrival, state.getTimestamp());
		return duration.compareTo(waitObjective.getWaitDuration()) >= 0;

	}

	private boolean isTravelObjectiveComplete(TravelObjective travelObjective, SpaceshipActorState state) {
		Key locationKey = state.getLocationKey();
		if (locationKey == null) {
			return false;
		}

		return locationKey.getName().equals(travelObjective.getStarKey());
	}

}
