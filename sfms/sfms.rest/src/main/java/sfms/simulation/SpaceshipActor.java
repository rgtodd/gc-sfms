package sfms.simulation;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import sfms.rest.db.schemas.DbEntity;
import sfms.simulation.json.Mission;

@SuppressWarnings("unused")
public class SpaceshipActor implements Actor {

	private Double m_x;
	private Double m_y;
	private Double m_z;
	private Double m_speed;
	private Instant m_asOf;

	@Override
	public ActorType getActorType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActorId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignMission(Mission mission) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Transaction txn = datastore.newTransaction();
		try {
			Query<Key> query = Query.newKeyQueryBuilder()
					.setKind("ShpMsn")
					.setFilter(PropertyFilter.hasAncestor(getEntityKey()))
					.build();

			QueryResults<Key> queryResults = txn.run(query);
			if (queryResults.hasNext()) {
				Key key = queryResults.next();
			} else {

			}
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	@Override
	public void updateStatus(Instant now) {
		// TODO Auto-generated method stub

	}

	@Override
	public Key getEntityKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
