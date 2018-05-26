package sfms.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import sfms.db.DbValueFactory;
import sfms.db.business.Region;
import sfms.db.business.RegionSet;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbStarField;
import sfms.simulator.json.Mission;
import sfms.simulator.json.Objective;
import sfms.simulator.json.TravelObjective;

public class MissionGenerator {

	private final Logger logger = Logger.getLogger(MissionGenerator.class.getName());

	private Datastore m_datastore = DatastoreOptions.getDefaultInstance().getService();
	private RegionSet m_sectors = RegionSet.loadSectors();
	private Random m_random = new Random();

	// HACK: Ignore actor type.
	//
	public Mission createMission(Actor actor) {

		List<Objective> objectives = new ArrayList<Objective>();
		int stopCount = 1; // 1 + m_random.nextInt(5);
		int objectiveId = 0;
		for (int stopIndex = 0; stopIndex < stopCount; ++stopIndex) {
			String starKey = getRandomStarKey();
			if (starKey != null) {
				TravelObjective objective = new TravelObjective();
				objective.setObjectiveId(++objectiveId);
				objective.setStarKey(starKey);

				objectives.add(objective);
			}
		}

		if (objectives.isEmpty()) {
			return null;
		}

		Mission mission = new Mission();
		mission.setObjectives(objectives);

		return mission;
	}

	// HACK: Return first star in random sector.
	//
	private String getRandomStarKey() {

		double x = m_random.nextDouble() * 2000 - 1000;
		double y = m_random.nextDouble() * 2000 - 1000;
		double z = m_random.nextDouble() * 2000 - 1000;
		Region sector = m_sectors.findClosestRegion(x, y, z);

		Key dbSectorKey = m_datastore.newKeyFactory()
				.setKind(DbEntity.Sector.getKind())
				.newKey(sector.getKey());
		logger.info("dbSectorKey = " + dbSectorKey.toString());

		Query<Key> dbStarKeyQuery = Query.newKeyQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.setFilter(PropertyFilter.eq(DbStarField.SectorKey.getName(),
						DbValueFactory.asValue(dbSectorKey)))
				.setLimit(1)
				.build();

		@SuppressWarnings("unused")
		QueryResults<Key> dbStarKeys = m_datastore.run(dbStarKeyQuery);

		return null;

		//
		// if (!dbStarKeys.hasNext()) {
		// return null;
		// }
		//
		// Key dbStarKey = dbStarKeys.next();
		//
		// String starKey = DbEntity.Star.createRestKey(dbStarKey);
		// return starKey;
	}
}