package sfms.rest.db.schemas;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

public class DbCrewMemberKey {

	private DbCrewMemberKey() {
	}

	public static Key createKey(Datastore datastore, String id) {
		return DbEntity.CrewMember.getRestKeyFactory().apply(datastore, DbEntity.CrewMember.getKind(), id);
	}
}
