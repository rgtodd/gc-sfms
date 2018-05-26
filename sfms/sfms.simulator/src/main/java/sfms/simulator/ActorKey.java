package sfms.simulator;

import com.google.cloud.datastore.Key;

public class ActorKey {

	private Key m_key;
	private String m_keyValue;

	public ActorKey(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Argument key is null.");
		}

		m_key = key;
		m_keyValue = key.toUrlSafe();
	}

	public Key getKey() {
		return m_key;
	}

	public String getKeyValue() {
		return m_keyValue;
	}

	@Override
	public String toString() {
		return m_keyValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_keyValue == null) ? 0 : m_keyValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorKey other = (ActorKey) obj;
		if (m_keyValue == null) {
			if (other.m_keyValue != null)
				return false;
		} else if (!m_keyValue.equals(other.m_keyValue))
			return false;
		return true;
	}

}
