package sfms.db;

import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;

/**
 * Factory class that parses strings into {@link StringValue} objects.
 *
 */
public class DbStringValueParser implements DbValueParser {

	private static final DbStringValueParser s_instance = new DbStringValueParser();

	public static DbStringValueParser getInstance() {
		return s_instance;
	}

	@Override
	public Value<?> parse(String text) {
		if (text == null || text.length() == 0) {
			return NullValue.of();
		}
		return StringValue.of(text);
	}

}
