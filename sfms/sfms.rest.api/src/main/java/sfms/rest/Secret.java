package sfms.rest;

import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class Secret {

	private final Logger logger = Logger.getLogger(Secret.class.getName());

	private final static String SECRET_BUCKET_NAME = "rgt-ssms.appspot.com";
	private final static String SECRET_FILE_NAME = "SECRET";
	private final static String REST_AUTHORIZATION_TOKEN_PROPERTY = "rest.authorization.token";

	public static final Secret INSTANCE = new Secret();

	private Properties m_properties;

	private Secret() {
	}

	public static String getRestAuthorizationToken() {
		return INSTANCE.getProperty(REST_AUTHORIZATION_TOKEN_PROPERTY);
	}

	public Properties getProperties() {
		if (m_properties == null) {
			synchronized (this) {
				if (m_properties == null) {
					m_properties = loadProperties();
				}
			}
		}

		return m_properties;
	}

	public String getProperty(String propertyName) {

		Properties properties = getProperties();

		// for (Object key : properties.keySet()) {
		// Object value = properties.get(key);
		// logger.log(Level.INFO, key.toString() + " : " + value.toString());
		// }

		String result = properties.getProperty(propertyName);

		// logger.log(Level.INFO, "propertyName = " + propertyName);
		// logger.log(Level.INFO, "result = " + result);

		return result;
	}

	private Properties loadProperties() {

		try {
			Storage storage = StorageOptions.getDefaultInstance().getService();
			BlobId blobId = BlobId.of(SECRET_BUCKET_NAME, SECRET_FILE_NAME);
			Blob blob = storage.get(blobId);
			byte[] bytes = blob.getContent();
			String token = new String(bytes, "US-ASCII");
			// logger.log(Level.INFO, "Secret properties: " + token);
			try (StringReader reader = new StringReader(token)) {
				Properties properties = new Properties();
				properties.load(reader);
				// for (Object key : properties.keySet()) {
				// Object value = properties.get(key);
				// logger.log(Level.INFO, key.toString() + " : " + value.toString());
				// }
				return properties;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Secrets could not be obtained.", e);
			return new Properties();
		}

	}

}
