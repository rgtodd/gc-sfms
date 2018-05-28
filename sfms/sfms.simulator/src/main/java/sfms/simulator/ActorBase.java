package sfms.simulator;

import java.time.Instant;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import sfms.db.DbKeyBuilder;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbMissionField;
import sfms.db.schemas.DbMissionStatusValues;
import sfms.simulator.json.Mission;

public abstract class ActorBase implements Actor {

	private final Logger logger = Logger.getLogger(ActorBase.class.getName());

	private Datastore m_datastore;
	private Entity m_dbEntity;
	private ActorKey m_actorKey;

	protected ActorBase(Datastore datastore, Entity dbEntity) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (dbEntity == null) {
			throw new IllegalArgumentException("Argument dbEntity is null.");
		}

		m_datastore = datastore;
		m_dbEntity = dbEntity;
		m_actorKey = new ActorKey(dbEntity.getKey());
	}

	@Override
	public ActorKey getActorKey() {
		return m_actorKey;
	}

	@Override
	public Mission getMission() {

		Entity dbMission = getMissionEntity();
		if (dbMission != null) {
			String jsonMission = dbMission.getString(DbMissionField.Mission.getName());
			return Mission.fromJson(jsonMission);
		}

		return null;
	}

	@Override
	public void assignMission(Instant now, Mission mission) {

		String jsonMission = mission.toJson();

		logger.info("Mission JSON = " + jsonMission);

		//
		// Create new state entity.
		//

		String key = DbKeyBuilder.create()
				.append(m_actorKey.getKey().getKind())
				.append(m_actorKey.getKey().getId())
				.appendDescendingSeconds(now)
				.build();

		Key dbKey = m_datastore.newKeyFactory()
				.setKind(DbEntity.Mission.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbMissionField.Mission.getName(), jsonMission)
				.set(DbMissionField.MissionStatus.getName(), DbMissionStatusValues.ACTIVE)
				.build();

		m_datastore.put(dbEntity);

		logger.info("Created mission for entity.  Key = " + key);
	}

	@Override
	public abstract void updateState(Instant now);

	@Override
	public abstract void initialize(Instant now, boolean reset);

	protected Datastore getDatastore() {
		return m_datastore;
	}

	protected Entity getEntity() {
		return m_dbEntity;
	}

	protected Entity getMissionEntity() {

		String keyPrefix = DbKeyBuilder.create()
				.append(m_actorKey.getKey().getKind())
				.append(m_actorKey.getKey().getId())
				.build();

		Entity dbMission = getFirstEntity(DbEntity.Mission.getKind(), keyPrefix);

		return dbMission;
	}

	protected Key getFirstEntityKey(String kind, String keyPrefix) {

		Key dbKeyPrefix = m_datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(1)
				.build();

		QueryResults<Key> dbKeys = m_datastore.run(dbKeyQuery);
		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (dbKey.getName().startsWith(keyPrefix)) {
				return dbKey;
			}
		}

		return null;
	}

	protected Entity getFirstEntity(String kind, String keyPrefix) {

		Key dbKeyPrefix = m_datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(1)
				.build();

		QueryResults<Key> dbKeys = m_datastore.run(dbKeyQuery);
		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (dbKey.getName().startsWith(keyPrefix)) {
				Entity dbEntity = m_datastore.get(dbKey);
				return dbEntity;
			}
		}

		return null;
	}

	protected void deleteEntities(String kind, String keyPrefix) {
		Key dbKeyPrefix = m_datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(3)
				.build();

		boolean historyExists = true;
		while (historyExists) {
			QueryResults<Key> dbKeys = m_datastore.run(dbKeyQuery);
			if (dbKeys.hasNext()) {
				historyExists = true; // assume success
				while (dbKeys.hasNext()) {
					Key dbKey = dbKeys.next();
					if (dbKey.getName().startsWith(keyPrefix)) {
						m_datastore.delete(dbKey);
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
