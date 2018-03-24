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
		String result = INSTANCE.getProperty(REST_AUTHORIZATION_TOKEN_PROPERTY);
		return result;
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
		String result = properties.getProperty(propertyName);
		return result;
	}

	private Properties loadProperties() {
		try {

			Storage storage = StorageOptions.getDefaultInstance().getService();
			BlobId blobId = BlobId.of(SECRET_BUCKET_NAME, SECRET_FILE_NAME);
			Blob blob = storage.get(blobId);

			byte[] contentBytes = blob.getContent();
			String content = new String(contentBytes, "US-ASCII");

			try (StringReader reader = new StringReader(content)) {
				Properties properties = new Properties();
				properties.load(reader);
				return properties;
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Secrets could not be obtained.", e);
			return new Properties();
		}
	}
}
