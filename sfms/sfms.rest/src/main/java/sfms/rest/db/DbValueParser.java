package sfms.rest.db;

import com.google.cloud.datastore.Value;

public interface DbValueParser {

	public Value<?> parse(String text);
}
