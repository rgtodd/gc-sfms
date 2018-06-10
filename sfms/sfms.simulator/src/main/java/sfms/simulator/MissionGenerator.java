package sfms.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import sfms.simulator.json.MissionDefinition;
import sfms.simulator.json.ObjectiveDefinition;
import sfms.simulator.json.TravelObjectiveDefinition;

public class MissionGenerator {

	private Datastore m_datastore = DatastoreOptions.getDefaultInstance().getService();
	private RegionSet m_sectors = RegionSet.loadSectors();
	private Random m_random = new Random();

	public MissionDefinition createMission(String actorKind) {

		if (actorKind.equals(DbEntity.Spaceship.getKind())) {
			return createSpaceshipMission();
		}

		if (actorKind.equals(DbEntity.CrewMember.getKind())) {
			return createCrewMemberMission();
		}

		throw new IllegalArgumentException("Unknown actor type.");
	}

	private MissionDefinition createSpaceshipMission() {
		List<ObjectiveDefinition> objectives = new ArrayList<ObjectiveDefinition>();
		int stopCount = 1 + m_random.nextInt(5);
		int objectiveId = 0;
		for (int stopIndex = 0; stopIndex < stopCount; ++stopIndex) {
			String starKey = getRandomStarKey();
			while (starKey == null) {
				starKey = getRandomStarKey();
			}

			TravelObjectiveDefinition objective = new TravelObjectiveDefinition();
			objective.setObjectiveId(++objectiveId);
			objective.setStarKey(starKey);

			objectives.add(objective);
		}

		MissionDefinition mission = new MissionDefinition();
		mission.setObjectives(objectives);

		return mission;
	}

	private MissionDefinition createCrewMemberMission() {
		return new MissionDefinition();
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

		Query<Key> dbStarKeyQuery = Query.newKeyQueryBuilder()
				.setKind(DbEntity.Star.getKind())
				.setFilter(PropertyFilter.eq(DbStarField.SectorKey.getName(),
						DbValueFactory.asValue(dbSectorKey)))
				.setLimit(1)
				.build();

		QueryResults<Key> dbStarKeys = m_datastore.run(dbStarKeyQuery);

		if (!dbStarKeys.hasNext()) {
			return null;
		}

		Key dbStarKey = dbStarKeys.next();

		String starKey = DbEntity.Star.createRestKey(dbStarKey);
		return starKey;
	}
}
