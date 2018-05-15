package sfms.simulation;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.db.schemas.DbEntity;

public class ActorDatasource {

	public ActorIterator getActors() {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		EntityQuery dbSpaceshipQuery = Query.newEntityQueryBuilder()
				.setKind(DbEntity.Spaceship.getKind())
				.build();

		QueryResults<Entity> dbSpaceships = datastore.run(dbSpaceshipQuery);

		return new SpaceshipActorIterator(dbSpaceships);
	}

	private static class SpaceshipActorIterator implements ActorIterator {

		private QueryResults<Entity> m_dbSpaceships;

		public SpaceshipActorIterator(QueryResults<Entity> dbSpaceships) {
			m_dbSpaceships = dbSpaceships;
		}

		@Override
		public boolean hasNext() {
			return m_dbSpaceships.hasNext();
		}

		@Override
		public Actor next() {

			return new SpaceshipActor(m_dbSpaceships.next());
		}

		@Override
		public void close() throws Exception {
			// No action required.
		}
	}
}
