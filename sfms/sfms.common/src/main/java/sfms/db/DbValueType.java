package sfms.db;

import com.google.cloud.datastore.Value;

/**
 * Specifies values types supported by the data store.
 *
 */
public enum DbValueType {

	Double(DbDoubleValueParser.getInstance()),
	Key(DbKeyValueParser.getInstance()),
	Long(DbLongValueParser.getInstance()),
	String(DbStringValueParser.getInstance()),
	Timestamp(DbTimestampValueParser.getInstance());

	private DbValueParser m_dbValueParser;

	private DbValueType(DbValueParser dbValueParser) {
		m_dbValueParser = dbValueParser;
	}

	public Value<?> parse(String text) {
		return m_dbValueParser.parse(text);
	}

}
