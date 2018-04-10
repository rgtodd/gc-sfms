package sfms.rest.api;

public class DeleteResult<TKey> {

	private TKey m_key;

	public TKey getKey() {
		return m_key;
	}

	public void setKey(TKey key) {
		m_key = key;
	}

}
