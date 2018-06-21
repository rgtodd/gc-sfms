package sfms.simulator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.CompositeKey;
import sfms.db.CompositeKeyBuilder;
import sfms.db.Db;
import sfms.db.DbEntityWrapper;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbCrewMemberStateField;
import sfms.db.schemas.DbEntity;

public class CrewMemberActorState {

	// Key fields
	//
	private long m_actorId;
	private Instant m_serialInstant;

	// Properties
	//
	private Instant m_timestamp;
	private Key m_locationKey;
	private Instant m_locationArrival;
	private Key m_destinationKey;

	public CrewMemberActorState(long actorId, Instant serialInstant) {
		if (serialInstant == null) {
			throw new IllegalArgumentException("Argument serialInstant is null.");
		}

		m_actorId = actorId;
		m_serialInstant = serialInstant;
	}

	public static CrewMemberActorState generateRandomState(long actorId, Instant now) {

		CrewMemberActorState state = new CrewMemberActorState(actorId, now);

		state.setTimestamp(now);

		return state;
	}

	public static CrewMemberActorState getCurrentState(Datastore datastore, long actorId) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorId)
				.build()
				.toString();

		DbEntityWrapper entity = DbEntityWrapper
				.wrap(Db.getFirstEntity(datastore, DbEntity.CrewMemberState.getKind(), keyPrefix));
		if (entity == null) {
			return null;
		}

		CompositeKey compositeKey = CompositeKey.parse(entity.getEntity().getKey().getName());
		Instant serialInstant = compositeKey.getFromSecondsDescending(1);

		CrewMemberActorState state = new CrewMemberActorState(actorId, serialInstant);

		state.setTimestamp(entity.getInstant(DbCrewMemberStateField.Timestamp));
		state.setLocationKey(entity.getKey(DbCrewMemberStateField.LocationKey));
		state.setLocationArrival(entity.getInstant(DbCrewMemberStateField.LocationArrival));
		state.setDestinationKey(entity.getKey(DbCrewMemberStateField.DestinationKey));

		return state;
	}

	public static boolean exists(Datastore datastore, long actorId) {
		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorId)
				.build()
				.toString();
		return Db.getFirstEntityKey(datastore, DbEntity.CrewMemberState.getKind(), keyPrefix) != null;
	}

	public static void reset(Datastore datastore, long actorId) {
		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorId)
				.build()
				.toString();
		Db.deleteEntities(datastore, DbEntity.CrewMemberState.getKind(), keyPrefix);
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

	public Key getDestinationKey() {
		return m_destinationKey;
	}

	public void setDestinationKey(Key destinationKey) {
		m_destinationKey = destinationKey;
	}

	public CrewMemberActorState apply(Duration duration) {

		//
		// Extract state information.
		//

		Instant timestamp = m_timestamp;
		Key locationKey = m_locationKey;
		Instant locationArrival = m_locationArrival;
		Key destinationKey = m_destinationKey;

		//
		// Apply effect of duration to state data.
		//

		timestamp = timestamp.plus(duration.getSeconds(), ChronoUnit.SECONDS);

		if (destinationKey != null) {
			locationKey = destinationKey;
			locationArrival = timestamp;
			destinationKey = null;
		}

		CrewMemberActorState updatedState = new CrewMemberActorState(getActorId(), timestamp);
		updatedState.setTimestamp(timestamp);
		updatedState.setLocationKey(locationKey);
		updatedState.setLocationArrival(locationArrival);
		updatedState.setDestinationKey(destinationKey);

		return updatedState;
	}

	public String save(Datastore datastore) {

		String key = CompositeKeyBuilder.create()
				.append(getActorId())
				.appendDescendingSeconds(getSerialInstant())
				.build()
				.toString();

		Key dbKey = datastore.newKeyFactory()
				.setKind(DbEntity.CrewMemberState.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbCrewMemberStateField.Timestamp.getName(), DbValueFactory.asValue(getTimestamp()))
				.set(DbCrewMemberStateField.LocationKey.getName(), DbValueFactory.asValue(getLocationKey()))
				.set(DbCrewMemberStateField.LocationArrival.getName(), DbValueFactory.asValue(getLocationArrival()))
				.set(DbCrewMemberStateField.DestinationKey.getName(), DbValueFactory.asValue(getDestinationKey()))
				.build();

		datastore.put(dbEntity);

		return key;
	}
}