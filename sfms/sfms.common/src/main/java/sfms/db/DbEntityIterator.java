package sfms.db;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.cloud.datastore.Entity;

public class DbEntityIterator implements Iterator<Entity> {

	private Iterator<Entity> m_iterator;
	private String m_keyPrefix;

	private Entity m_nextEntity = null;

	public DbEntityIterator(Iterator<Entity> iterator, String keyPrefix) {
		if (iterator == null) {
			throw new IllegalArgumentException("Argument iterator is null.");
		}
		if (keyPrefix == null) {
			throw new IllegalArgumentException("Argument keyPrefix is null.");
		}

		m_iterator = iterator;
		m_keyPrefix = keyPrefix;
	}

	@Override
	public boolean hasNext() {

		if (m_nextEntity != null) {
			return true;
		}

		if (!m_iterator.hasNext()) {
			return false;
		}

		Entity entity = m_iterator.next();
		if (!entity.getKey().getName().startsWith(m_keyPrefix)) {
			return false;
		}

		m_nextEntity = entity;
		return true;
	}

	@Override
	public Entity next() {

		if (m_nextEntity != null) {
			Entity result = m_nextEntity;
			m_nextEntity = null;
			return result;
		}

		Entity entity = m_iterator.next();
		if (!entity.getKey().getName().startsWith(m_keyPrefix)) {
			throw new NoSuchElementException();
		}

		return entity;
	}

}
