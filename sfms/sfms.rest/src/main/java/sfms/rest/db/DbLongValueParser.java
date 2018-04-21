package sfms.rest.db;

import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Value;

public class DbLongValueParser implements DbValueParser {

	private static final DbLongValueParser s_instance = new DbLongValueParser();

	public static DbLongValueParser getInstance() {
		return s_instance;
	}

	@Override
	public Value<?> parse(String text) {
		if (text == null || text.length() == 0) {
			return NullValue.of();
		}
		return LongValue.of(Long.parseLong(text));
	}

}
