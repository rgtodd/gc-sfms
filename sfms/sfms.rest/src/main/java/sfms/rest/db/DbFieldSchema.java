package sfms.rest.db;

import com.google.cloud.datastore.Value;

/**
 * Interface associated with entity field enumerations.
 *
 */
public interface DbFieldSchema {

	public String getName();

	public String getTitle();

	public String getDescription();

	public Value<?> parseValue(String text);

}
