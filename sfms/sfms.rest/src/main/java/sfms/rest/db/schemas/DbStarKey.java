package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

public class DbStarKey {

	private DbStarKey() {
	}

	public static Key createKey(Datastore datastore, String id) {
		return DbEntity.Star.getRestKeyFactory().apply(datastore, DbEntity.Star.getKind(), id);
	}
}
