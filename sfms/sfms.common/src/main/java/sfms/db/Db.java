package sfms.db;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

public class Db {

	private Db() {
	}

	public static Key getFirstEntityKey(Datastore datastore, String kind, String keyPrefix) {

		Key dbKeyPrefix = datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(1)
				.build();

		QueryResults<Key> dbKeys = datastore.run(dbKeyQuery);
		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (dbKey.getName().startsWith(keyPrefix)) {
				return dbKey;
			}
		}

		return null;
	}

	public static Entity getFirstEntity(Datastore datastore, String kind, String keyPrefix) {

		Key dbKeyPrefix = datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(1)
				.build();

		QueryResults<Key> dbKeys = datastore.run(dbKeyQuery);
		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (dbKey.getName().startsWith(keyPrefix)) {
				Entity dbEntity = datastore.get(dbKey);
				return dbEntity;
			}
		}

		return null;
	}

	public static void deleteEntities(Datastore datastore, String kind, String keyPrefix) {
		Key dbKeyPrefix = datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.setLimit(3)
				.build();

		boolean historyExists = true;
		while (historyExists) {
			QueryResults<Key> dbKeys = datastore.run(dbKeyQuery);
			if (dbKeys.hasNext()) {
				historyExists = true; // assume success
				while (dbKeys.hasNext()) {
					Key dbKey = dbKeys.next();
					if (dbKey.getName().startsWith(keyPrefix)) {
						datastore.delete(dbKey);
					} else {
						historyExists = false;
						break;
					}
				}
			} else {
				historyExists = false;
			}
		}
	}
}
