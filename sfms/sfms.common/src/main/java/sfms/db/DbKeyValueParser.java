package sfms.db;

import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Value;

/**
 * Factory class that parses strings into {@link KeyValue} objects.
 *
 */
public class DbKeyValueParser implements DbValueParser {

	private static final DbKeyValueParser s_instance = new DbKeyValueParser();

	public static DbKeyValueParser getInstance() {
		return s_instance;
	}

	@Override
	public Value<?> parse(String text) {
		if (text == null || text.length() == 0) {
			return NullValue.of();
		}
		return KeyValue.of(Key.fromUrlSafe(text));
	}

}
