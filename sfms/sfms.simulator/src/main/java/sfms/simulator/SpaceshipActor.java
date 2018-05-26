package sfms.simulator;

import java.time.Instant;
import java.util.Random;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import sfms.db.DbKeyBuilder;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbMissionField;
import sfms.db.schemas.DbSpaceshipStateField;
import sfms.simulator.json.Mission;

public class SpaceshipActor implements Actor {

	private static final Random RANDOM = new Random();

	private final Logger logger = Logger.getLogger(SpaceshipActor.class.getName());

	private ActorKey m_key;
	private Entity m_dbSpaceship;

	public SpaceshipActor(Entity dbSpaceship) {
		if (dbSpaceship == null) {
			throw new IllegalArgumentException("dbSpaceship is null.");
		}
		if (!dbSpaceship.getKey().getKind().equals(DbEntity.Spaceship.getKind())) {
			throw new IllegalArgumentException("dbSpaceship is not spaceship.");
		}

		m_key = new ActorKey(dbSpaceship.getKey());
		m_dbSpaceship = dbSpaceship;
	}

	@Override
	public ActorKey getKey() {
		return m_key;
	}

	@Override
	public void assignMission(Mission mission) {

		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerFor(Mission.class);
		String jsonMission;
		try {
			jsonMission = writer.writeValueAsString(mission);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		logger.info("Mission JSON = " + jsonMission);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSpaceshipKey = m_dbSpaceship.getKey();

		Key dbMissionKey = Key.newBuilder(dbSpaceshipKey, DbEntity.Mission.getKind(), "ACTIVE").build();

		Entity dbMission = Entity.newBuilder(dbMissionKey)
				.set(DbMissionField.Mission.getName(), jsonMission)
				.set(DbMissionField.MissionStatus.getName(), "ACTIVE")
				.build();

		Transaction txn = datastore.newTransaction();
		try {

			Query<Key> dbMissionQuery = Query.newKeyQueryBuilder()
					.setKind(DbEntity.Mission.getKind())
					.setFilter(PropertyFilter.hasAncestor(dbSpaceshipKey))
					.build();

			QueryResults<Key> dbMissions = txn.run(dbMissionQuery);
			if (dbMissions.hasNext()) {
				// TODO: Mission already exists.
			} else {
				logger.info("Putting new mission entity.");
				txn.put(dbMission);
			}

			logger.info("Committing transaction.");
			txn.commit();
		} finally {
			if (txn.isActive()) {
				logger.info("Rolling back transaction.");
				txn.rollback();
			}
		}
	}

	@Override
	public void updateState(Instant now) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(Instant now, boolean reset) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		//
		// Check if a state entity exists..
		//
		if (reset) {
			resetHistory(datastore);
		} else {
			if (historyExists(datastore)) {
				return;
			}
		}

		//
		// Create new state entity.
		//

		String key = DbKeyBuilder.create()
				.append(m_key.getKey().getId())
				.appendDescendingSeconds(now)
				.build();

		Key dbKey = datastore.newKeyFactory()
				.setKind(DbEntity.SpaceshipState.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbSpaceshipStateField.Timestamp.getName(),
						DbValueFactory.asValue(Timestamp.ofTimeSecondsAndNanos(now.getEpochSecond(), now.getNano())))
				.set(DbSpaceshipStateField.LocationEntity.getName(), NullValue.of())
				.set(DbSpaceshipStateField.LocationX.getName(), DbValueFactory.asValue(getRandomCoordinate()))
				.set(DbSpaceshipStateField.LocationY.getName(), DbValueFactory.asValue(getRandomCoordinate()))
				.set(DbSpaceshipStateField.LocationZ.getName(), DbValueFactory.asValue(getRandomCoordinate()))
				.set(DbSpaceshipStateField.Speed.getName(), NullValue.of())
				.set(DbSpaceshipStateField.DestinationEntity.getName(), NullValue.of())
				.set(DbSpaceshipStateField.DestinationX.getName(), NullValue.of())
				.set(DbSpaceshipStateField.DestinationY.getName(), NullValue.of())
				.set(DbSpaceshipStateField.DestinationZ.getName(), NullValue.of())
				.build();

		datastore.put(dbEntity);

		logger.info("Created initial state for space ship.  Key = " + key);
	}

	private long getRandomCoordinate() {
		return RANDOM.nextInt(4000) - 2000;
	}

	private boolean historyExists(Datastore datastore) {

		String keyPrefix = DbKeyBuilder.create()
				.append(m_key.getKey().getId())
				.build();

		Key dbKeyPrefix = datastore.newKeyFactory()
				.setKind(DbEntity.SpaceshipState.getKind())
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(DbEntity.SpaceshipState.getKind())
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(1)
				.build();

		QueryResults<Key> dbKeys = datastore.run(dbKeyQuery);
		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (dbKey.getName().startsWith(keyPrefix)) {
				return true;
			}
		}

		return false;
	}

	private void resetHistory(Datastore datastore) {
		String keyPrefix = DbKeyBuilder.create()
				.append(m_key.getKey().getId())
				.build();

		Key dbKeyPrefix = datastore.newKeyFactory()
				.setKind(DbEntity.SpaceshipState.getKind())
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(DbEntity.SpaceshipState.getKind())
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(3)
				.build();

		boolean historyExists = true;
		while (historyExists) {
			QueryResults<Key> dbKeys = datastore.run(dbKeyQuery);
			if (dbKeys.hasNext()) {
				historyExists = true; // assume success
				while (dbKeys.hasNext()) {
					Key dbKey = dbKeys.next();
					if (dbKey.getName().startsWith(keyPrefix)) {
						datastore.delete(dbKey);
					} else {
						historyExists = false;
						break;
					}
				}
			} else {
				historyExists = false;
			}
		}
	}
}
