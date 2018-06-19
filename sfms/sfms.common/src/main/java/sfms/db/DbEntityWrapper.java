package sfms.db;

import java.time.Instant;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Key;

public class DbEntityWrapper {

	private BaseEntity<Key> m_entity;

	private DbEntityWrapper(BaseEntity<Key> entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Argument entity is null.");
		}

		m_entity = entity;
	}

	public BaseEntity<Key> getEntity() {
		return m_entity;
	}

	public static DbEntityWrapper wrap(BaseEntity<Key> entity) {
		if (entity == null) {
			return null;
		}

		return new DbEntityWrapper(entity);
	}

	public Key getKey(DbFieldSchema field) {
		String name = field.getName();
		if (!m_entity.contains(name)) {
			return null;
		}
		if (m_entity.isNull(name)) {
			return null;
		}
		return m_entity.getKey(name);
	}

	public String getString(DbFieldSchema field) {
		String name = field.getName();
		if (!m_entity.contains(name)) {
			return null;
		}
		if (m_entity.isNull(name)) {
			return null;
		}
		return m_entity.getString(name);
	}

	public Double getDouble(DbFieldSchema field) {
		String name = field.getName();
		if (!m_entity.contains(name)) {
			return null;
		}
		if (m_entity.isNull(name)) {
			return null;
		}
		return m_entity.getDouble(name);
	}

	public Long getLong(DbFieldSchema field) {
		String name = field.getName();
		if (!m_entity.contains(name)) {
			return null;
		}
		if (m_entity.isNull(name)) {
			return null;
		}
		return m_entity.getLong(name);
	}

	public Timestamp getTimestamp(DbFieldSchema field) {
		String name = field.getName();
		if (!m_entity.contains(name)) {
			return null;
		}
		if (m_entity.isNull(name)) {
			return null;
		}
		return m_entity.getTimestamp(name);
	}

	public Instant getInstant(DbFieldSchema field) {
		Timestamp timestamp = getTimestamp(field);
		if (timestamp == null) {
			return null;
		}
		return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
	}
}
