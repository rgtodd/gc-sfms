package sfms.simulation;

import java.time.Instant;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbMissionField;
import sfms.simulation.json.Mission;

public class SpaceshipActor implements Actor {

	private final Logger logger = Logger.getLogger(SpaceshipActor.class.getName());

	private Entity m_dbSpaceship;

	public SpaceshipActor(Entity dbSpaceship) {
		m_dbSpaceship = dbSpaceship;
	}

	@Override
	public void assignMission(Mission mission) {

		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerFor(Mission.class);
		String jsonMission;
		try {
			jsonMission = writer.writeValueAsString(mission);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		logger.info("Mission JSON = " + jsonMission);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSpaceshipKey = getEntityKey();

		Key dbMissionKey = Key.newBuilder(dbSpaceshipKey, DbEntity.Mission.getKind(), "ACTIVE").build();

		Entity dbMission = Entity.newBuilder(dbMissionKey)
				.set(DbMissionField.Mission.getName(), jsonMission)
				.set(DbMissionField.MissionStatus.getName(), "ACTIVE")
				.build();

		Transaction txn = datastore.newTransaction();
		try {

			Query<Key> dbMissionQuery = Query.newKeyQueryBuilder()
					.setKind(DbEntity.Mission.getKind())
					.setFilter(PropertyFilter.hasAncestor(dbSpaceshipKey))
					.build();

			QueryResults<Key> dbMissions = txn.run(dbMissionQuery);
			if (dbMissions.hasNext()) {
				// TODO: Mission already exists.
			} else {
				logger.info("Putting new mission entity.");
				txn.put(dbMission);
			}

			logger.info("Committing transaction.");
			txn.commit();
		} finally {
			if (txn.isActive()) {
				logger.info("Rolling back transaction.");
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
		return m_dbSpaceship.getKey();
	}

}
