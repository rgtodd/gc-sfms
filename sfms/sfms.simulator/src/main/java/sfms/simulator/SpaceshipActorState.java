package sfms.simulator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.Db;
import sfms.db.DbEntityWrapper;
import sfms.db.CompositeKey;
import sfms.db.CompositeKeyBuilder;
import sfms.db.DbValueFactory;
import sfms.db.business.Coordinates;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSpaceshipStateField;

class SpaceshipActorState {

	private static final Random RANDOM = new Random();
	private static final int SECONDS_PER_DAY = 60 * 60 * 24;

	// Key fields
	//
	private long m_actorId;
	private Instant m_serialInstant;

	// Properties
	//
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

	public SpaceshipActorState(long actorId, Instant serialInstant) {
		if (serialInstant == null) {
			throw new IllegalArgumentException("Argument serialInstant is null.");
		}

		m_actorId = actorId;
		m_serialInstant = serialInstant;
	}

	public static SpaceshipActorState generateRandomState(long actorId, Instant now) {

		SpaceshipActorState state = new SpaceshipActorState(actorId, now);

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

	public static SpaceshipActorState getCurrentState(Datastore datastore, long actorId) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorId)
				.build()
				.toString();

		DbEntityWrapper entity = DbEntityWrapper
				.wrap(Db.getFirstEntity(datastore, DbEntity.SpaceshipState.getKind(), keyPrefix));
		if (entity == null) {
			return null;
		}

		CompositeKey compositeKey = CompositeKey.parse(entity.getEntity().getKey().getName());
		Instant serialInstant = compositeKey.getFromSecondsDescending(1);

		SpaceshipActorState state = new SpaceshipActorState(actorId, serialInstant);

		state.setTimestamp(entity.getInstant(DbSpaceshipStateField.Timestamp));
		state.setLocationX(entity.getDouble(DbSpaceshipStateField.LocationX));
		state.setLocationY(entity.getDouble(DbSpaceshipStateField.LocationY));
		state.setLocationZ(entity.getDouble(DbSpaceshipStateField.LocationZ));
		state.setSpeed(entity.getDouble(DbSpaceshipStateField.Speed));
		state.setLocationKey(entity.getKey(DbSpaceshipStateField.LocationKey));
		state.setLocationArrival(entity.getInstant(DbSpaceshipStateField.LocationArrival));
		state.setMissionKey(entity.getKey(DbSpaceshipStateField.MissionKey));
		state.setObjectiveIndex(entity.getLong(DbSpaceshipStateField.ObjectiveIndex));
		state.setDestinationKey(entity.getKey(DbSpaceshipStateField.DestinationKey));
		state.setDestinationX(entity.getDouble(DbSpaceshipStateField.DestinationX));
		state.setDestinationY(entity.getDouble(DbSpaceshipStateField.DestinationY));
		state.setDestinationZ(entity.getDouble(DbSpaceshipStateField.DestinationZ));
		state.setWaitDayCount(entity.getLong(DbSpaceshipStateField.WaitDayCount));

		return state;
	}

	public static boolean exists(Datastore datastore, long actorId) {
		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorId)
				.build()
				.toString();
		return Db.getFirstEntityKey(datastore, DbEntity.SpaceshipState.getKind(), keyPrefix) != null;
	}

	public static void reset(Datastore datastore, long actorId) {
		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorId)
				.build()
				.toString();
		Db.deleteEntities(datastore, DbEntity.SpaceshipState.getKind(), keyPrefix);
	}

	public long getActorId() {
		return m_actorId;
	}

	public Instant getSerialInstant() {
		return m_serialInstant;
	}

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

	public SpaceshipActorState apply(Duration duration) {

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

		SpaceshipActorState state = new SpaceshipActorState(getActorId(), timestamp);

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

	public String save(Datastore datastore) {

		String key = CompositeKeyBuilder.create()
				.append(getActorId())
				.appendDescendingSeconds(getSerialInstant())
				.build()
				.toString();

		Key dbKey = datastore.newKeyFactory()
				.setKind(DbEntity.SpaceshipState.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbSpaceshipStateField.Timestamp.getName(), DbValueFactory.asValue(getTimestamp()))
				.set(DbSpaceshipStateField.LocationX.getName(), DbValueFactory.asValue(getLocationX()))
				.set(DbSpaceshipStateField.LocationY.getName(), DbValueFactory.asValue(getLocationY()))
				.set(DbSpaceshipStateField.LocationZ.getName(), DbValueFactory.asValue(getLocationZ()))
				.set(DbSpaceshipStateField.Speed.getName(), DbValueFactory.asValue(getSpeed()))
				.set(DbSpaceshipStateField.LocationKey.getName(), DbValueFactory.asValue(getLocationKey()))
				.set(DbSpaceshipStateField.LocationArrival.getName(),
						DbValueFactory.asValue(getLocationArrival()))
				.set(DbSpaceshipStateField.MissionKey.getName(), DbValueFactory.asValue(getMissionKey()))
				.set(DbSpaceshipStateField.ObjectiveIndex.getName(), DbValueFactory.asValue(getObjectiveIndex()))
				.set(DbSpaceshipStateField.DestinationKey.getName(), DbValueFactory.asValue(getDestinationKey()))
				.set(DbSpaceshipStateField.DestinationX.getName(), DbValueFactory.asValue(getDestinationX()))
				.set(DbSpaceshipStateField.DestinationY.getName(), DbValueFactory.asValue(getDestinationY()))
				.set(DbSpaceshipStateField.DestinationZ.getName(), DbValueFactory.asValue(getDestinationZ()))
				.set(DbSpaceshipStateField.WaitDayCount.getName(), DbValueFactory.asValue(getWaitDayCount()))
				.build();

		datastore.put(dbEntity);

		return key;
	}

	private static Double getRandomCoordinate() {
		return (double) (RANDOM.nextInt(4000) - 2000);
	}
}