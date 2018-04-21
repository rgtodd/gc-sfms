package sfms.rest.db;

import com.google.cloud.datastore.Value;

public enum DbValueType {

	Long(DbLongValueParser.getInstance());

	private DbValueParser m_dbValueParser;

	private DbValueType(DbValueParser dbValueParser) {
		m_dbValueParser = dbValueParser;
	}

	public Value<?> parse(String text) {
		return m_dbValueParser.parse(text);
	}

}
