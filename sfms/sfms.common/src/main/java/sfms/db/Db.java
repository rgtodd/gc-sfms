package sfms.db;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

public class Db {

	private Db() {
	}

	public static Key getFirstEntityKey(Datastore datastore, String kind, String keyPrefix) {

		QueryResults<Key> dbKeys;
		if (keyPrefix != null) {
			Key dbKeyPrefix = datastore.newKeyFactory()
					.setKind(kind)
					.newKey(keyPrefix);

			Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
					.setKind(kind)
					.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
					.setLimit(1)
					.build();

			dbKeys = datastore.run(dbKeyQuery);
		} else {
			Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
					.setKind(kind)
					.setLimit(1)
					.build();

			dbKeys = datastore.run(dbKeyQuery);
		}

		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (keyPrefix != null) {
				if (dbKey.getName().startsWith(keyPrefix)) {
					return dbKey;
				}
			} else {
				return dbKey;
			}
		}

		return null;
	}

	public static Entity getFirstEntity(Datastore datastore, String kind, String keyPrefix) {

		QueryResults<Key> dbKeys;
		if (keyPrefix != null) {
			Key dbKeyPrefix = datastore.newKeyFactory()
					.setKind(kind)
					.newKey(keyPrefix);

			Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
					.setKind(kind)
					.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
					.setLimit(1)
					.build();

			dbKeys = datastore.run(dbKeyQuery);
		} else {
			Query<Key> dbKeyQuery = Query.newKeyQueryBuilder()
					.setKind(kind)
					.setLimit(1)
					.build();

			dbKeys = datastore.run(dbKeyQuery);
		}

		if (dbKeys.hasNext()) {
			Key dbKey = dbKeys.next();
			if (keyPrefix != null) {
				if (dbKey.getName().startsWith(keyPrefix)) {
					Entity dbEntity = datastore.get(dbKey);
					return dbEntity;
				}
			} else {
				Entity dbEntity = datastore.get(dbKey);
				return dbEntity;
			}
		}

		return null;
	}

	public static void deleteEntities(Datastore datastore, String kind, String keyPrefix) {

		Query<Key> dbKeyQuery;
		if (keyPrefix != null) {
			Key dbKeyPrefix = datastore.newKeyFactory()
					.setKind(kind)
					.newKey(keyPrefix);

			dbKeyQuery = Query.newKeyQueryBuilder()
					.setKind(kind)
					.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
					.setLimit(3)
					.build();
		} else {
			dbKeyQuery = Query.newKeyQueryBuilder()
					.setKind(kind)
					.setLimit(100)
					.build();
		}

		boolean historyExists = true;
		while (historyExists) {
			QueryResults<Key> dbKeys = datastore.run(dbKeyQuery);
			if (dbKeys.hasNext()) {
				historyExists = true; // assume success
				while (dbKeys.hasNext()) {
					Key dbKey = dbKeys.next();
					if (keyPrefix != null) {
						if (dbKey.getName().startsWith(keyPrefix)) {
							datastore.delete(dbKey);
						} else {
							historyExists = false;
							break;
						}
					} else {
						datastore.delete(dbKey);
					}
				}
			} else {
				historyExists = false;
			}
		}
	}

	public static DbEntityIterator getEntities(Datastore datastore, String kind, String keyPrefix) {

		Key dbKeyPrefix = datastore.newKeyFactory()
				.setKind(kind)
				.newKey(keyPrefix);

		EntityQuery dbMissionQuery = Query.newEntityQueryBuilder()
				.setKind(kind)
				.setFilter(PropertyFilter.ge("__key__", dbKeyPrefix))
				.build();

		QueryResults<Entity> dbMissions = datastore.run(dbMissionQuery);

		return new DbEntityIterator(dbMissions, keyPrefix);
	}
}
