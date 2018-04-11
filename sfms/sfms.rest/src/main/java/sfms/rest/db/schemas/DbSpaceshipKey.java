package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

public class DbSpaceshipKey {

	private DbSpaceshipKey() {
	}

	public static Key createKey(Datastore datastore, String id) {
		return DbEntity.Spaceship.getRestKeyFactory().apply(datastore, DbEntity.Spaceship.getKind(), id);
	}
}
