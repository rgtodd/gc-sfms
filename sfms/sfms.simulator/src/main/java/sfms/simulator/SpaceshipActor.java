package sfms.simulator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.DbEntityWrapper;
import sfms.db.DbKeyBuilder;
import sfms.db.DbValueFactory;
import sfms.db.business.Coordinates;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSpaceshipStateField;

public class SpaceshipActor extends ActorBase implements Actor {

	private static final Random RANDOM = new Random();
	static final int SECONDS_PER_DAY = 60 * 60 * 24;

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
			resetHistory();
		} else {
			if (historyExists()) {
				return;
			}
		}

		//
		// Create new state entity.
		//

		State state = generateRandomState(now);
		String key = saveState(state);

		logger.info("Created initial state for space ship.  Key = " + key);
	}

	@Override
	public void updateState(Instant now) {

		State currentState = getCurrentState();
		if (currentState == null) {
			return;
		}

		Duration duration = Duration.between(currentState.getTimestamp(), now);

		State updatedState = currentState.apply(duration);

		saveState(updatedState);
	}

	private State getCurrentState() {
		String keyPrefix = DbKeyBuilder.create()
				.append(getActorKey().getKey().getId())
				.build();

		Entity entity = getFirstEntity(DbEntity.SpaceshipState.getKind(), keyPrefix);
		if (entity == null) {
			return null;
		}

		DbEntityWrapper wrapper = new DbEntityWrapper(entity);
		State state = new State();
		state.setTimestamp(wrapper.getInstant(DbSpaceshipStateField.Timestamp));
		state.setLocationX(wrapper.getDouble(DbSpaceshipStateField.LocationX));
		state.setLocationY(wrapper.getDouble(DbSpaceshipStateField.LocationY));
		state.setLocationZ(wrapper.getDouble(DbSpaceshipStateField.LocationZ));
		state.setSpeed(wrapper.getDouble(DbSpaceshipStateField.Speed));
		state.setLocationKey(wrapper.getKey(DbSpaceshipStateField.LocationKey));
		state.setLocationArrival(wrapper.getInstant(DbSpaceshipStateField.LocationArrival));
		state.setMissionKey(wrapper.getKey(DbSpaceshipStateField.MissionKey));
		state.setObjectiveIndex(wrapper.getLong(DbSpaceshipStateField.ObjectiveIndex));
		state.setDestinationKey(wrapper.getKey(DbSpaceshipStateField.DestinationKey));
		state.setDestinationX(wrapper.getDouble(DbSpaceshipStateField.DestinationX));
		state.setDestinationY(wrapper.getDouble(DbSpaceshipStateField.DestinationY));
		state.setDestinationZ(wrapper.getDouble(DbSpaceshipStateField.DestinationZ));
		state.setWaitDayCount(wrapper.getLong(DbSpaceshipStateField.WaitDayCount));

		return state;

	}

	private String saveState(State state) {

		String key = DbKeyBuilder.create()
				.append(getActorKey().getKey().getId())
				.appendDescendingSeconds(state.getTimestamp())
				.build();

		Key dbKey = getDatastore().newKeyFactory()
				.setKind(DbEntity.SpaceshipState.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbSpaceshipStateField.Timestamp.getName(), DbValueFactory.asValue(state.getTimestamp()))
				.set(DbSpaceshipStateField.LocationX.getName(), DbValueFactory.asValue(state.getLocationX()))
				.set(DbSpaceshipStateField.LocationY.getName(), DbValueFactory.asValue(state.getLocationY()))
				.set(DbSpaceshipStateField.LocationZ.getName(), DbValueFactory.asValue(state.getLocationZ()))
				.set(DbSpaceshipStateField.Speed.getName(), DbValueFactory.asValue(state.getSpeed()))
				.set(DbSpaceshipStateField.LocationKey.getName(), DbValueFactory.asValue(state.getLocationKey()))
				.set(DbSpaceshipStateField.LocationArrival.getName(),
						DbValueFactory.asValue(state.getLocationArrival()))
				.set(DbSpaceshipStateField.MissionKey.getName(), DbValueFactory.asValue(state.getMissionKey()))
				.set(DbSpaceshipStateField.ObjectiveIndex.getName(), DbValueFactory.asValue(state.getObjectiveIndex()))
				.set(DbSpaceshipStateField.DestinationKey.getName(), DbValueFactory.asValue(state.getDestinationKey()))
				.set(DbSpaceshipStateField.DestinationX.getName(), DbValueFactory.asValue(state.getDestinationX()))
				.set(DbSpaceshipStateField.DestinationY.getName(), DbValueFactory.asValue(state.getDestinationY()))
				.set(DbSpaceshipStateField.DestinationZ.getName(), DbValueFactory.asValue(state.getDestinationZ()))
				.set(DbSpaceshipStateField.WaitDayCount.getName(), DbValueFactory.asValue(state.getWaitDayCount()))
				.build();

		getDatastore().put(dbEntity);
		return key;
	}

	private State generateRandomState(Instant now) {

		State state = new State();
		state.setTimestamp(now);
		state.setLocationX(getRandomCoordinate());
		state.setLocationY(getRandomCoordinate());
		state.setLocationZ(getRandomCoordinate());
		state.setSpeed(1.0 + RANDOM.nextDouble() * 9.0);
		state.setDestinationX(getRandomCoordinate());
		state.setDestinationY(getRandomCoordinate());
		state.setDestinationZ(getRandomCoordinate());

		return state;
	}

	private Double getRandomCoordinate() {
		return (double) (RANDOM.nextInt(4000) - 2000);
	}

	private boolean historyExists() {
		String keyPrefix = DbKeyBuilder.create()
				.append(getActorKey().getKey().getId())
				.build();
		return getFirstEntityKey(DbEntity.SpaceshipState.getKind(), keyPrefix) != null;
	}

	private void resetHistory() {
		String keyPrefix = DbKeyBuilder.create()
				.append(getActorKey().getKey().getId())
				.build();
		deleteEntities(DbEntity.SpaceshipState.getKind(), keyPrefix);
	}

	public static class State {
		private Instant m_timestamp;
		private Double m_locationX;
		private Double m_locationY;
		private Double m_locationZ;
		private Double m_speed;
		private Key m_locationKey;
		private Instant m_locationArrival;
		private Key m_missionKey;
		private Long m_objectiveIndex;
		private Key m_destinationKey;
		private Double m_destinationX;
		private Double m_destinationY;
		private Double m_destinationZ;
		private Long m_waitDayCount;

		public Instant getTimestamp() {
			return m_timestamp;
		}

		public void setTimestamp(Instant timestamp) {
			m_timestamp = timestamp;
		}

		public Double getLocationX() {
			return m_locationX;
		}

		public void setLocationX(Double locationX) {
			m_locationX = locationX;
		}

		public Double getLocationY() {
			return m_locationY;
		}

		public void setLocationY(Double locationY) {
			m_locationY = locationY;
		}

		public Double getLocationZ() {
			return m_locationZ;
		}

		public void setLocationZ(Double locationZ) {
			m_locationZ = locationZ;
		}

		public Double getSpeed() {
			return m_speed;
		}

		public void setSpeed(Double speed) {
			m_speed = speed;
		}

		public Key getLocationKey() {
			return m_locationKey;
		}

		public void setLocationKey(Key locationKey) {
			m_locationKey = locationKey;
		}

		public Instant getLocationArrival() {
			return m_locationArrival;
		}

		public void setLocationArrival(Instant locationArrival) {
			m_locationArrival = locationArrival;
		}

		public Key getMissionKey() {
			return m_missionKey;
		}

		public void setMissionKey(Key missionKey) {
			m_missionKey = missionKey;
		}

		public Long getObjectiveIndex() {
			return m_objectiveIndex;
		}

		public void setObjectiveIndex(Long objectiveIndex) {
			m_objectiveIndex = objectiveIndex;
		}

		public Key getDestinationKey() {
			return m_destinationKey;
		}

		public void setDestinationKey(Key destinationKey) {
			m_destinationKey = destinationKey;
		}

		public Double getDestinationX() {
			return m_destinationX;
		}

		public void setDestinationX(Double destinationX) {
			m_destinationX = destinationX;
		}

		public Double getDestinationY() {
			return m_destinationY;
		}

		public void setDestinationY(Double destinationY) {
			m_destinationY = destinationY;
		}

		public Double getDestinationZ() {
			return m_destinationZ;
		}

		public void setDestinationZ(Double destinationZ) {
			m_destinationZ = destinationZ;
		}

		public Long getWaitDayCount() {
			return m_waitDayCount;
		}

		public void setWaitDayCount(Long waitDayCount) {
			m_waitDayCount = waitDayCount;
		}

		public State apply(Duration duration) {

			//
			// Extract state information.
			//

			Instant timestamp = m_timestamp;
			Double locationX = m_locationX;
			Double locationY = m_locationY;
			Double locationZ = m_locationZ;
			Double speed = m_speed;
			Key locationKey = m_locationKey;
			Instant locationArrival = m_locationArrival;
			Key missionKey = m_missionKey;
			Long objectiveIndex = m_objectiveIndex;
			Key destinationKey = m_destinationKey;
			Double destinationX = m_destinationX;
			Double destinationY = m_destinationY;
			Double destinationZ = m_destinationZ;
			Long waitDayCount = m_waitDayCount;

			boolean hasLocation = locationX != null && locationY != null && locationZ != null;
			boolean hasSpeed = speed != null;
			boolean hasDestination = destinationX != null && destinationY != null && destinationZ != null;

			Coordinates location = hasLocation ? new Coordinates(locationX, locationY, locationZ) : null;
			Coordinates destination = hasDestination ? new Coordinates(destinationX, destinationY, destinationZ) : null;

			//
			// Apply effect of duration to state data.
			//

			timestamp = timestamp.plus(duration.getSeconds(), ChronoUnit.SECONDS);

			if (hasLocation && hasSpeed) {

				double distanceTraveled = speed * duration.getSeconds() / SECONDS_PER_DAY;

				if (hasDestination) {

					double distanceToDestination = Coordinates.getDistance(location, destination);
					if (distanceTraveled >= distanceToDestination) {

						// We've arrived at our destination.
						//
						location = destination;
						speed = 0.0;

					} else {

						// Keep traveling.
						//
						Coordinates delta = Coordinates.getVector(location, destination).normalize()
								.scale(distanceTraveled);
						location = Coordinates.add(location, delta);
					}
				} else {

					// Keep traveling.
					//
					Coordinates delta = Coordinates.getVector(location, destination).normalize()
							.scale(distanceTraveled);
					location = Coordinates.add(location, delta);
				}
			}

			if (location != null) {
				locationX = location.getX();
				locationY = location.getY();
				locationZ = location.getZ();
			}

			//
			// Create new state
			//

			State state = new State();
			state.setTimestamp(timestamp);
			state.setLocationX(locationX);
			state.setLocationY(locationY);
			state.setLocationZ(locationZ);
			state.setSpeed(speed);
			state.setLocationKey(locationKey);
			state.setLocationArrival(locationArrival);
			state.setMissionKey(missionKey);
			state.setObjectiveIndex(objectiveIndex);
			state.setDestinationKey(destinationKey);
			state.setDestinationX(destinationX);
			state.setDestinationY(destinationY);
			state.setDestinationZ(destinationZ);
			state.setWaitDayCount(waitDayCount);

			return state;
		}
	}
}
