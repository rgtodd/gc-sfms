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
import sfms.db.schemas.DbMissionStateField;

public class MissionState {

	public static final MissionState NULL = new MissionState();

	// Key fields
	//
	private String m_actorKind;
	private long m_actorId;
	private Instant m_missionSerialInstant;
	private Instant m_stateSerialInstant;

	// Properties
	//
	private Instant m_timestamp;
	private Long m_objectiveIndex;
	private Instant m_startTimestamp;
	private Instant m_endTimestamp;

	private MissionState() {
	}

	public MissionState(String actorKind, long actorId, Instant missionSerialInstant, Instant stateSerialInstant) {
		if (actorKind == null) {
			throw new IllegalArgumentException("Argument actorKind is null.");
		}
		if (missionSerialInstant == null) {
			throw new IllegalArgumentException("Argument missionSerialInstant is null.");
		}
		if (stateSerialInstant == null) {
			throw new IllegalArgumentException("Argument stateSerialInstant is null.");
		}

		m_actorKind = actorKind;
		m_actorId = actorId;
		m_missionSerialInstant = missionSerialInstant;
		m_stateSerialInstant = stateSerialInstant;
	}

	public static MissionState getCurrentMissionState(Datastore datastore, String actorKind, long actorId,
			Instant missionSerialInstant) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(actorKind)
				.append(actorId)
				.appendDescendingSeconds(missionSerialInstant)
				.build()
				.toString();

		DbEntityWrapper entity = DbEntityWrapper
				.wrap(Db.getFirstEntity(datastore, DbEntity.MissionState.getKind(), keyPrefix));
		if (entity == null) {
			return null;
		}

		CompositeKey compositeKey = CompositeKey.parse(entity.getEntity().getKey().getName());
		Instant stateSerialInstant = compositeKey.getFromSecondsDescending(3);

		MissionState result = new MissionState(actorKind, actorId, missionSerialInstant, stateSerialInstant);

		result.setTimestamp(entity.getInstant(DbMissionStateField.Timestamp));
		result.setObjectiveIndex(entity.getLong(DbMissionStateField.ObjectiveIndex));
		result.setStartTimestamp(entity.getInstant(DbMissionStateField.StartTimestamp));
		result.setEndTimestamp(entity.getInstant(DbMissionStateField.EndTimestamp));

		return result;
	}

	public String getActorKind() {
		return m_actorKind;
	}

	public long getActorId() {
		return m_actorId;
	}

	public Instant getMissionSerialInstant() {
		return m_missionSerialInstant;
	}

	public Instant getStateSerialInstant() {
		return m_stateSerialInstant;
	}

	public Instant getTimestamp() {
		return m_timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		m_timestamp = timestamp;
	}

	public Long getObjectiveIndex() {
		return m_objectiveIndex;
	}

	public void setObjectiveIndex(Long objectiveIndex) {
		m_objectiveIndex = objectiveIndex;
	}

	public Instant getStartTimestamp() {
		return m_startTimestamp;
	}

	public void setStartTimestamp(Instant startTimestamp) {
		m_startTimestamp = startTimestamp;
	}

	public Instant getEndTimestamp() {
		return m_endTimestamp;
	}

	public void setEndTimestamp(Instant endTimestamp) {
		m_endTimestamp = endTimestamp;
	}

	public void save(Datastore datastore) {

		String key = CompositeKeyBuilder.create()
				.append(getActorKind())
				.append(getActorId())
				.appendDescendingSeconds(getMissionSerialInstant())
				.appendDescendingSeconds(getStateSerialInstant())
				.build()
				.toString();

		Key dbKey = datastore.newKeyFactory()
				.setKind(DbEntity.MissionState.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbMissionStateField.Timestamp.getName(), DbValueFactory.asValue(getTimestamp()))
				.set(DbMissionStateField.ObjectiveIndex.getName(), DbValueFactory.asValue(getObjectiveIndex()))
				.set(DbMissionStateField.StartTimestamp.getName(), DbValueFactory.asValue(getStartTimestamp()))
				.set(DbMissionStateField.EndTimestamp.getName(), DbValueFactory.asValue(getEndTimestamp()))
				.build();

		datastore.put(dbEntity);
	}

}
