package sfms.db;

import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.Timestamp;

/**
 * Factory class that parses strings into {@link StringValue} objects.
 *
 */
public class DbTimestampValueParser implements DbValueParser {

	private static final DbTimestampValueParser s_instance = new DbTimestampValueParser();

	public static DbTimestampValueParser getInstance() {
		return s_instance;
	}

	@Override
	public Value<?> parse(String text) {
		if (text == null || text.length() == 0) {
			return NullValue.of();
		}
		return TimestampValue.of(Timestamp.parseTimestamp(text));
	}

}
