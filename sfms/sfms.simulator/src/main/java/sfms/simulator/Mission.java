package sfms.simulator;

import java.time.Instant;

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
import sfms.simulator.json.MissionDefinition;

public class Mission {

	public static final Mission NULL = new Mission();

	// Key fields
	//
	private String m_actorKind;
	private long m_actorId;
	private Instant m_serialInstant;

	// Properties
	//
	private MissionDefinition m_missionDefinition;
	private String m_status;

	private Mission() {
	}

	public Mission(String actorKind, long actorId, Instant serialInstant) {
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

	public static Mission getCurrentMission(Datastore datastore, String actorKind, long actorId) {

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

		Mission result = new Mission(actorKind, actorId, serialInstant);
		result.setMissionDefinition(createMissionFromJson(entity.getString(DbMissionField.MissionDefinition)));
		result.setStatus(entity.getString(DbMissionField.MissionStatus));

		return result;
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

	public MissionDefinition getMissionDefinition() {
		return m_missionDefinition;
	}

	public void setMissionDefinition(MissionDefinition missionDefinition) {
		m_missionDefinition = missionDefinition;
	}

	public String getStatus() {
		return m_status;
	}

	public void setStatus(String status) {
		m_status = status;
	}

	public void save(Datastore datastore) {

		String jsonMission = getMissionDefinition().toJson();

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
				.set(DbMissionField.MissionDefinition.getName(), DbValueFactory.asValue(jsonMission))
				.set(DbMissionField.MissionStatus.getName(), DbValueFactory.asValue(getStatus()))
				.build();

		datastore.put(dbEntity);
	}

	private static MissionDefinition createMissionFromJson(String jsonMission) {
		if (jsonMission == null) {
			return null;
		}

		return MissionDefinition.fromJson(jsonMission);
	}

}
