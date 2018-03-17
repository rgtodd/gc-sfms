package sfms.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SfmsProperties {

	public static final String APPLICATION = "application.properties";
	public static final String SFMS_REST_HOST = "sfms.rest.host";

	public static final SfmsProperties INSTANCE = new SfmsProperties();

	private ConcurrentMap<String, Properties> m_properties = new ConcurrentHashMap<String, Properties>();

	private SfmsProperties() {
	}

	public Properties getProperties(String fileName) {
		if (!m_properties.containsKey(fileName)) {
			m_properties.putIfAbsent(fileName, loadProperties(fileName));
		}
		return m_properties.get(fileName);
	}

	public String getProperty(String fileName, String key) {
		return getProperties(fileName).getProperty(key);
	}

	private Properties loadProperties(String fileName) {
		InputStream inputStream = SfmsProperties.class.getClassLoader().getResourceAsStream(fileName);
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
