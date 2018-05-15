package sfms.simulation;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbMissionField;

@SuppressWarnings("unused")
public class ActorDatasource {

	public ActorIterator getActiveActors() {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key key;

		EntityQuery query = Query.newEntityQueryBuilder()
				.setKind(DbEntity.Mission.getKind())
				.setFilter(PropertyFilter.eq(DbMissionField.MissionStatus.getName(), MissionStatus.ACTIVE))
				.build();

		QueryResults<Entity> queryResults = datastore.run(query);
		while (queryResults.hasNext()) {
			Entity dbCrewMemberMission = queryResults.next();
		}

		return null;
	}

	public ActorIterator getInactiveActors() {
		return null;
	}

}
