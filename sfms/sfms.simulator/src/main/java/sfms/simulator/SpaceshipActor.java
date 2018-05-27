package sfms.simulator;

import java.time.Instant;
import java.util.Random;
import java.util.logging.Logger;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.DbKeyBuilder;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSpaceshipStateField;
import sfms.simulator.json.Mission;

public class SpaceshipActor extends ActorBase implements Actor {

	private static final Random RANDOM = new Random();

	private final Logger logger = Logger.getLogger(SpaceshipActor.class.getName());

	public SpaceshipActor(Datastore datastore, Entity dbEntity) {
		super(datastore, dbEntity);

		if (!dbEntity.getKey().getKind().equals(DbEntity.Spaceship.getKind())) {
			throw new IllegalArgumentException("dbEntity is not spaceship.");
		}
	}

	@Override
	public ActorKey getActorKey() {
		return getActorKeyBase();
	}

	@Override
	public Mission getMission() {
		return getMissionBase();
	}

	@Override
	public void assignMission(Instant now, Mission mission) {
		assignMissionBase(now, mission);
	}

	@Override
	public void updateState(Instant now) {
		// TODO Auto-generated method stub

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

		String key = DbKeyBuilder.create()
				.append(getActorKeyBase().getKey().getId())
				.appendDescendingSeconds(now)
				.build();

		Key dbKey = getDatastoreBase().newKeyFactory()
				.setKind(DbEntity.SpaceshipState.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbSpaceshipStateField.Timestamp.getName(),
						DbValueFactory.asValue(Timestamp.ofTimeSecondsAndNanos(state.getTimestamp().getEpochSecond(),
								state.getTimestamp().getNano())))
				.set(DbSpaceshipStateField.LocationEntity.getName(), DbValueFactory.asValue(state.getLocationKey()))
				.set(DbSpaceshipStateField.LocationX.getName(), DbValueFactory.asValue(state.getLocationX()))
				.set(DbSpaceshipStateField.LocationY.getName(), DbValueFactory.asValue(state.getLocationY()))
				.set(DbSpaceshipStateField.LocationZ.getName(), DbValueFactory.asValue(state.getLocationZ()))
				.set(DbSpaceshipStateField.Speed.getName(), DbValueFactory.asValue(state.getSpeed()))
				.set(DbSpaceshipStateField.DestinationEntity.getName(),
						DbValueFactory.asValue(state.getDestinationKey()))
				.set(DbSpaceshipStateField.DestinationX.getName(), DbValueFactory.asValue(state.getDestinationX()))
				.set(DbSpaceshipStateField.DestinationY.getName(), DbValueFactory.asValue(state.getDestinationY()))
				.set(DbSpaceshipStateField.DestinationZ.getName(), DbValueFactory.asValue(state.getDestinationZ()))
				.build();

		getDatastoreBase().put(dbEntity);

		logger.info("Created initial state for space ship.  Key = " + key);
	}

	private State generateRandomState(Instant now) {
		State state = new State();
		state.setTimestamp(now);
		state.setLocationX(getRandomCoordinate());
		state.setLocationY(getRandomCoordinate());
		state.setLocationZ(getRandomCoordinate());
		return state;
	}

	private Double getRandomCoordinate() {
		return (double) (RANDOM.nextInt(4000) - 2000);
	}

	private boolean historyExists() {
		String keyPrefix = DbKeyBuilder.create()
				.append(getActorKeyBase().getKey().getId())
				.build();
		return getFirstEntityKey(DbEntity.SpaceshipState.getKind(), keyPrefix) != null;
	}

	private void resetHistory() {
		String keyPrefix = DbKeyBuilder.create()
				.append(getActorKeyBase().getKey().getId())
				.build();
		deleteEntities(DbEntity.SpaceshipState.getKind(), keyPrefix);
	}

	public static class State {
		private Instant m_timestamp;
		private Key m_locationKey;
		private Double m_locationX;
		private Double m_locationY;
		private Double m_locationZ;
		private Double m_speed;
		private Key m_destinationKey;
		private Double m_destinationX;
		private Double m_destinationY;
		private double m_destinationZ;

		public Instant getTimestamp() {
			return m_timestamp;
		}

		public void setTimestamp(Instant timestamp) {
			m_timestamp = timestamp;
		}

		public Key getLocationKey() {
			return m_locationKey;
		}

		public void setLocationKey(Key locationKey) {
			m_locationKey = locationKey;
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

		public double getDestinationZ() {
			return m_destinationZ;
		}

		public void setDestinationZ(double destinationZ) {
			m_destinationZ = destinationZ;
		}
	}
}
