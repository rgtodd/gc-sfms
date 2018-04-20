package sfms.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import sfms.storage.Storage;

public class Secret {

	private final Logger logger = Logger.getLogger(Secret.class.getName());

	private final static String SECRET_BUCKET_NAME = Constants.CLOUD_STORAGE_BUCKET;
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

			try (ReadableByteChannel readChannel = Storage.getManager().getReadableByteChannel(SECRET_BUCKET_NAME,
					SECRET_FILE_NAME);
					InputStream inputStream = Channels.newInputStream(readChannel);
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "US-ASCII");
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

				Properties properties = new Properties();
				properties.load(bufferedReader);
				return properties;
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Secrets could not be obtained.", e);
			return new Properties();
		}
	}
}
