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
import sfms.db.schemas.DbSimulationField;

public class Simulation {

	public static final Simulation NULL = new Simulation();

	// Key fields
	//
	private Instant m_serialInstant;

	// Properties
	//
	private Instant m_timestamp;

	private Simulation() {
	}

	public Simulation(Instant serialInstant) {
		if (serialInstant == null) {
			throw new IllegalArgumentException("Argument serialInstant is null.");
		}

		m_serialInstant = serialInstant;
	}

	public static Simulation getCurrentSimulation(Datastore datastore) {

		DbEntityWrapper entity = DbEntityWrapper
				.wrap(Db.getFirstEntity(datastore, DbEntity.Simulation.getKind(), null));
		if (entity == null) {
			return null;
		}

		CompositeKey compositeKey = CompositeKey.parse(entity.getEntity().getKey().getName());
		Instant serialInstant = compositeKey.getFromSecondsDescending(0);

		Simulation result = new Simulation(serialInstant);
		result.setTimestamp(entity.getInstant(DbSimulationField.Timestamp));

		return result;
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

	public void save(Datastore datastore) {

		String key = CompositeKeyBuilder.create()
				.appendDescendingSeconds(getSerialInstant())
				.build()
				.toString();

		Key dbKey = datastore.newKeyFactory()
				.setKind(DbEntity.Simulation.getKind())
				.newKey(key);

		Entity dbEntity = Entity.newBuilder(dbKey)
				.set(DbSimulationField.Timestamp.getName(), DbValueFactory.asValue(getTimestamp()))
				.build();

		datastore.put(dbEntity);
	}

}
