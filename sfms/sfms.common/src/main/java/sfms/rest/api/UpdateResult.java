package sfms.rest.api;

/**
 * Defines the response returned by REST service update methods.
 *
 * @param <TKey>
 *            the key type of the updated entity.
 * 
 */
public class UpdateResult<TKey> {

	private TKey m_key;

	public TKey getKey() {
		return m_key;
	}

	public void setKey(TKey key) {
		m_key = key;
	}

}
