package sfms.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PropertyFile {

	// Standard property files.
	//
	public static final String APPLICATION = "application.properties";

	public static final String SERVER_TYPE = "server.type";
	private static final String SERVER_TYPE_DEVELOPMENT = "development";
	private static final String SERVER_TYPE_PRODUCTION = "production";
	private static final String SERVER_TYPE_DEFAULT = SERVER_TYPE_PRODUCTION;

	public static final String SFMS_REST_HOST = "sfms.rest.host";

	public static final PropertyFile INSTANCE = new PropertyFile();

	private ConcurrentMap<String, Properties> m_properties = new ConcurrentHashMap<String, Properties>();

	private PropertyFile() {
	}

	public String getProperty(String fileName, String key, String defaultValue) {
		return valueOrDefault(getProperties(fileName).getProperty(key), defaultValue);
	}

	public String getServerProperty(String fileName, String key) {
		return getProperties(fileName).getProperty(key + "." + getServerType());
	}

	public String getServerType() {
		return getProperty(APPLICATION, SERVER_TYPE, SERVER_TYPE_DEFAULT);
	}

	public boolean isProduction() {
		return getServerType().equals(SERVER_TYPE_PRODUCTION);
	}

	public boolean isDevelopment() {
		return getServerType().equals(SERVER_TYPE_DEVELOPMENT);
	}

	private static String valueOrDefault(String value, String defaultValue) {
		if (value != null) {
			return value;
		}

		return defaultValue;
	}

	private Properties getProperties(String fileName) {
		if (!m_properties.containsKey(fileName)) {
			m_properties.putIfAbsent(fileName, loadProperties(fileName));
		}
		return m_properties.get(fileName);
	}

	private Properties loadProperties(String fileName) {
		InputStream inputStream = PropertyFile.class.getClassLoader().getResourceAsStream(fileName);
		if (inputStream == null) {
			return null;
		}
		try {
			Properties properties = new Properties();
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				return null;
			}

			return properties;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

}
