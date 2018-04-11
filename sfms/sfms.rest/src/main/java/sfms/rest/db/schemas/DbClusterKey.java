package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

public class DbClusterKey {

	private DbClusterKey() {
	}

	public static Key createKey(Datastore datastore, String id) {
		return DbEntity.Cluster.getRestKeyFactory().apply(datastore, DbEntity.Cluster.getKind(), id);
	}
}
