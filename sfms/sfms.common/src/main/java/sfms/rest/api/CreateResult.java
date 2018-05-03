package sfms.rest.api;

/**
 * Defines the response returned by REST service create methods.
 *
 * @param <TKey>
 *            the key type of the created entity.
 * 
 */
public class CreateResult<TKey> {

	private TKey m_key;

	public TKey getKey() {
		return m_key;
	}

	public void setKey(TKey key) {
		m_key = key;
	}

}
