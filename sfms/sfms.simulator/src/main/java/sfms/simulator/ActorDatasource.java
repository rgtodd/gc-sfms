package sfms.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import sfms.db.schemas.DbEntity;

public class ActorDatasource {

	private Datastore m_datastore;

	public ActorDatasource(Datastore datastore) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null");
		}

		m_datastore = datastore;
	}

	public Actor getActor(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Argument key is null.");
		}

		Entity dbActor = m_datastore.get(key);

		if (dbActor.getKey().getKind().equals(DbEntity.CrewMember.getKind())) {
			return new CrewMemberActor(m_datastore, dbActor);
		}

		if (dbActor.getKey().getKind().equals(DbEntity.Spaceship.getKind())) {
			return new SpaceshipActor(m_datastore, dbActor);
		}

		throw new IllegalArgumentException("Unknown actor key type.");
	}

	public ActorIterator getActors() {

		List<ActorIterator> iterators = new ArrayList<ActorIterator>();

		// Retrieve spaceships.
		{
			EntityQuery dbSpaceshipQuery = Query.newEntityQueryBuilder()
					.setKind(DbEntity.Spaceship.getKind())
					.build();

			QueryResults<Entity> dbSpaceships = m_datastore.run(dbSpaceshipQuery);

			iterators.add(new SpaceshipActorIterator(m_datastore, dbSpaceships));
		}

		// Retrieve crew members.
		{
			EntityQuery dbCrewMemberQuery = Query.newEntityQueryBuilder()
					.setKind(DbEntity.CrewMember.getKind())
					.build();

			QueryResults<Entity> dbCrewMembers = m_datastore.run(dbCrewMemberQuery);

			iterators.add(new CrewMemberActorIterator(m_datastore, dbCrewMembers));
		}

		return new CompositeActorIterator(iterators);
	}

	private static class CompositeActorIterator implements ActorIterator {

		private List<ActorIterator> m_iterators;

		public CompositeActorIterator(List<ActorIterator> iterators) {
			m_iterators = iterators;
		}

		@Override
		public boolean hasNext() {

			while (!m_iterators.isEmpty()) {
				ActorIterator iterator = m_iterators.get(0);
				if (iterator.hasNext()) {
					return true;
				}
				m_iterators.remove(0);
			}

			return false;
		}

		@Override
		public Actor next() {

			while (!m_iterators.isEmpty()) {
				try {
					return m_iterators.get(0).next();
				} catch (NoSuchElementException e) {
					m_iterators.remove(0);
				}
			}

			throw new NoSuchElementException();
		}

		@Override
		public void close() throws Exception {
			// No action required.
		}

	}

	private static class SpaceshipActorIterator implements ActorIterator {

		private Datastore m_datastore;
		private QueryResults<Entity> m_dbSpaceships;

		public SpaceshipActorIterator(Datastore datastore, QueryResults<Entity> dbSpaceships) {
			m_datastore = datastore;
			m_dbSpaceships = dbSpaceships;
		}

		@Override
		public boolean hasNext() {
			return m_dbSpaceships.hasNext();
		}

		@Override
		public Actor next() {
			return new SpaceshipActor(m_datastore, m_dbSpaceships.next());
		}

		@Override
		public void close() throws Exception {
			// No action required.
		}
	}

	private static class CrewMemberActorIterator implements ActorIterator {

		private Datastore m_datastore;
		private QueryResults<Entity> m_dbCrewMembers;

		public CrewMemberActorIterator(Datastore datastore, QueryResults<Entity> dbCrewMembers) {
			m_datastore = datastore;
			m_dbCrewMembers = dbCrewMembers;
		}

		@Override
		public boolean hasNext() {
			return m_dbCrewMembers.hasNext();
		}

		@Override
		public Actor next() {
			return new CrewMemberActor(m_datastore, m_dbCrewMembers.next());
		}

		@Override
		public void close() throws Exception {
			// No action required.
		}
	}
}