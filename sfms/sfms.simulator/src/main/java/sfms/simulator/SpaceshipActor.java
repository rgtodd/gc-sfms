package sfms.simulator;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;

import sfms.db.schemas.DbEntity;

public class SpaceshipActor extends ActorBase implements Actor {

	private final Logger logger = Logger.getLogger(SpaceshipActor.class.getName());

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

		logger.info("Created initial state for space ship.  Key = " + key);
	}

	@Override
	public void updateState(Instant now) {

		SpaceshipActorState currentState = SpaceshipActorState.getCurrentState(getDatastore(), getActorId());
		if (currentState == null) {
			return;
		}

		Duration duration = Duration.between(currentState.getTimestamp(), now);

		SpaceshipActorState updatedState = currentState.apply(duration);

		updatedState.save(getDatastore());
	}

}
