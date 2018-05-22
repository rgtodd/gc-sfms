package sfms.db;

import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Value;

/**
 * Factory class that parses strings into {@link DoubleValue} objects.
 *
 */
public class DbDoubleValueParser implements DbValueParser {

	private static final DbDoubleValueParser s_instance = new DbDoubleValueParser();

	public static DbDoubleValueParser getInstance() {
		return s_instance;
	}

	@Override
	public Value<?> parse(String text) {
		if (text == null || text.length() == 0) {
			return NullValue.of();
		}
		return DoubleValue.of(Double.parseDouble(text));
	}

}
