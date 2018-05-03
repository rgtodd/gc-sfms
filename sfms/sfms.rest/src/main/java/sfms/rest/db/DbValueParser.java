package sfms.rest.db;

import com.google.cloud.datastore.Value;

/**
 * Interface for objects that parse strings into {@link Value} objects.
 *
 */
public interface DbValueParser {

	public Value<?> parse(String text);
}
