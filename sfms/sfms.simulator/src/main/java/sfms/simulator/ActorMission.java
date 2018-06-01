package sfms.simulator;

import java.time.Instant;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.CompositeKey;
import sfms.db.CompositeKeyBuilder;
import sfms.db.Db;
import sfms.db.DbEntityWrapper;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbMissionField;
import sfms.simulator.json.Mission;

public class ActorMission {

	private final Logger logger = Logger.getLogger(ActorMission.class.getName());

	public static final ActorMission NULL = new ActorMission();

	// Key fields
	//
	private String m_actorKind;
	private long m_actorId;
	private Instant m_serialInstant;

	// Properties
	//
	private Mission m_mission;
	private String m_status;

	private ActorMission() {
	}

	public ActorMission(String actorKind, long actorId, Instant serialInstant) {
		if (actorKind == null) {
			throw new IllegalArgumentException("Argument actorKind is null.");
		}
		if (serialInstant == null) {
			throw new IllegalArgumentException("Argument serialInstant is null.");
		}

		m_actorKind = actorKind;
		m_actorId = actorId;
		m_serialInstant = serialInstant;
	}

	public static ActorMission getCurrentMission(Datastore datastore, String actorKind, long actorId) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorKind)
				.append(actorId)
				.build()
				.toString();

		DbEntityWrapper entity = DbEntityWrapper
				.wrap(Db.getFirstEntity(datastore, DbEntity.Mission.getKind(), keyPrefix));
		if (entity == null) {
			return null;
		}

		CompositeKey compositeKey = CompositeKey.parse(entity.getEntity().getKey().getName());
		Instant serialInstant = compositeKey.getFromSecondsDescending(2);

		ActorMission result = new ActorMission(actorKind, actorId, serialInstant);
		result.setMission(createMissionFromJson(entity.getString(DbMissionField.Mission)));
		result.setStatus(entity.getString(DbMissionField.MissionStatus));

		return result;
	}

	private static Mission createMissionFromJson(String jsonMission) {
		if (jsonMission == null) {
			return null;
		}

		return Mission.fromJson(jsonMission);
	}

	public String getActorKind() {
		return m_actorKind;
	}

	public long getActorId() {
		return m_actorId;
	}

	public Instant getSerialInstant() {
		return m_serialInstant;
	}

	public Mission getMission() {
		return m_mission;
	}

	public void setMission(Mission mission) {
		m_mission = mission;
	}

	public String getStatus() {
		return m_status;
	}

	public void setStatus(String status) {
		m_status = status;
	}

	public void save(Datastore datastore) {

		String jsonMission = getMission().toJson();
		logger.info("Mission JSON = " + jsonMission);

		String key = CompositeKeyBuilder.create()
				.append(getActorKind())
				.append(getActorId())
				.appendDescendingSeconds(getSerialInstant())
				.build()
				.toString();

		Key dbKey = datastore.newKeyFactory()
				.setKind(DbEntity.Mission.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbMissionField.Mission.getName(), DbValueFactory.asValue(jsonMission))
				.set(DbMissionField.MissionStatus.getName(), DbValueFactory.asValue(getStatus()))
				.build();

		datastore.put(dbEntity);

		logger.info("Created mission for entity.  Key = " + key);
	}

}
