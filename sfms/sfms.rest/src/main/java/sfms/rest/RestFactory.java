package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import sfms.db.DbEntityWrapper;
import sfms.db.schemas.DbClusterField;
import sfms.db.schemas.DbCrewMemberField;
import sfms.db.schemas.DbCrewMemberStateField;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbMissionField;
import sfms.db.schemas.DbMissionStateField;
import sfms.db.schemas.DbSectorField;
import sfms.db.schemas.DbSpaceshipField;
import sfms.db.schemas.DbSpaceshipStateField;
import sfms.db.schemas.DbStarField;
import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.CrewMemberState;
import sfms.rest.api.models.Mission;
import sfms.rest.api.models.MissionObjective;
import sfms.rest.api.models.MissionState;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.SpaceshipState;
import sfms.rest.api.models.Star;
import sfms.simulator.json.MissionDefinition;
import sfms.simulator.json.ObjectiveDefinition;

/**
 * Factory used to create REST entities from data store entities.
 * 
 */
public class RestFactory {

	private final Logger logger = Logger.getLogger(RestFactory.class.getName());

	public Cluster createCluster(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Cluster result = new Cluster();
		result.setKey(DbEntity.Cluster.createRestKey(entity.getKey()));
		result.setClusterPartition(wrapper.getLong(DbClusterField.ClusterPartition));
		result.setClusterX(wrapper.getLong(DbClusterField.ClusterX));
		result.setClusterY(wrapper.getLong(DbClusterField.ClusterY));
		result.setClusterZ(wrapper.getLong(DbClusterField.ClusterZ));
		result.setMinimumX(wrapper.getLong(DbClusterField.MinimumX));
		result.setMinimumY(wrapper.getLong(DbClusterField.MinimumY));
		result.setMinimumZ(wrapper.getLong(DbClusterField.MinimumZ));
		result.setMaximumX(wrapper.getLong(DbClusterField.MaximumX));
		result.setMaximumY(wrapper.getLong(DbClusterField.MaximumY));
		result.setMaximumZ(wrapper.getLong(DbClusterField.MaximumZ));
		return result;
	}

	public List<Cluster> createClusters(Iterator<BaseEntity<Key>> entities) {
		List<Cluster> result = new ArrayList<Cluster>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createCluster(entity));
		}
		return result;
	}

	public Sector createSector(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Sector result = new Sector();
		result.setKey(DbEntity.Sector.createRestKey(entity.getKey()));
		result.setSectorX(wrapper.getLong(DbSectorField.SectorX));
		result.setSectorY(wrapper.getLong(DbSectorField.SectorY));
		result.setSectorZ(wrapper.getLong(DbSectorField.SectorZ));
		result.setMinimumX(wrapper.getLong(DbSectorField.MinimumX));
		result.setMinimumY(wrapper.getLong(DbSectorField.MinimumY));
		result.setMinimumZ(wrapper.getLong(DbSectorField.MinimumZ));
		result.setMaximumX(wrapper.getLong(DbSectorField.MaximumX));
		result.setMaximumY(wrapper.getLong(DbSectorField.MaximumY));
		result.setMaximumZ(wrapper.getLong(DbSectorField.MaximumZ));
		return result;
	}

	public List<Sector> createSectors(Iterator<BaseEntity<Key>> entities) {
		List<Sector> result = new ArrayList<Sector>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createSector(entity));
		}
		return result;
	}

	public Star createStar(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Star result = new Star();
		result.setKey(DbEntity.Star.createRestKey(entity.getKey()));
		result.setCatalogId(String.valueOf(wrapper.getLong(DbStarField.CatalogId)));
		result.setClusterKey(DbEntity.Cluster.createRestKey(wrapper.getKey(DbStarField.ClusterKey)));
		result.setSectorKey(DbEntity.Sector.createRestKey(wrapper.getKey(DbStarField.SectorKey)));
		result.setHipparcosId(wrapper.getString(DbStarField.HipparcosId));
		result.setHenryDraperId(wrapper.getString(DbStarField.HenryDraperId));
		result.setHarvardRevisedId(wrapper.getString(DbStarField.HarvardRevisedId));
		result.setGlieseId(wrapper.getString(DbStarField.GlieseId));
		result.setBayerFlamsteedId(wrapper.getString(DbStarField.BayerFlamsteedId));
		result.setProperName(wrapper.getString(DbStarField.ProperName));
		result.setRightAscension(wrapper.getDouble(DbStarField.RightAscension));
		result.setDeclination(wrapper.getDouble(DbStarField.Declination));
		result.setDistance(wrapper.getDouble(DbStarField.Distance));
		result.setProperMotionRightAscension(wrapper.getDouble(DbStarField.ProperMotionRightAscension));
		result.setProperMotionDeclination(wrapper.getDouble(DbStarField.ProperMotionDeclination));
		result.setRadialVelocity(wrapper.getDouble(DbStarField.RadialVelocity));
		result.setMagnitude(wrapper.getDouble(DbStarField.Magnitude));
		result.setAbsoluteMagnitude(wrapper.getDouble(DbStarField.AbsoluteMagnitude));
		result.setSpectrum(wrapper.getString(DbStarField.Spectrum));
		result.setColorIndex(wrapper.getDouble(DbStarField.ColorIndex));
		result.setX(wrapper.getDouble(DbStarField.X));
		result.setY(wrapper.getDouble(DbStarField.Y));
		result.setZ(wrapper.getDouble(DbStarField.Z));
		result.setVX(wrapper.getDouble(DbStarField.VX));
		result.setVY(wrapper.getDouble(DbStarField.VY));
		result.setVZ(wrapper.getDouble(DbStarField.VZ));
		result.setRightAcensionRadians(wrapper.getDouble(DbStarField.RightAcensionRadians));
		result.setDeclinationRadians(wrapper.getDouble(DbStarField.DeclinationRadians));
		result.setProperMotionRightAscensionRadians(wrapper.getDouble(DbStarField.ProperMotionRightAscensionRadians));
		result.setProperMotionDeclinationRadians(wrapper.getDouble(DbStarField.ProperMotionDeclinationRadians));
		result.setBayerId(wrapper.getString(DbStarField.BayerId));
		result.setFlamsteed(wrapper.getString(DbStarField.Flamsteed));
		result.setConstellation(wrapper.getString(DbStarField.Constellation));
		result.setCompanionStarId(wrapper.getString(DbStarField.CompanionStarId));
		result.setPrimaryStarId(wrapper.getString(DbStarField.PrimaryStarId));
		result.setMultipleStarId(wrapper.getString(DbStarField.MultipleStarId));
		result.setLuminosity(wrapper.getDouble(DbStarField.Luminosity));
		result.setVariableStarDesignation(wrapper.getString(DbStarField.VariableStarDesignation));
		result.setVariableMinimum(wrapper.getDouble(DbStarField.VariableMinimum));
		result.setVariableMaximum(wrapper.getDouble(DbStarField.VariableMaximum));

		return result;
	}

	public List<Star> createStars(Iterator<BaseEntity<Key>> entities) {
		List<Star> result = new ArrayList<Star>();

		int count = 0;
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createStar(entity));

			if ((++count) % 1000 == 0) {
				logger.info("createStars count = " + count);
			}
		}

		return result;
	}

	public Spaceship createSpaceship(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Spaceship result = new Spaceship();
		result.setKey(DbEntity.Spaceship.createRestKey(entity.getKey()));
		result.setName(wrapper.getString(DbSpaceshipField.Name));

		return result;
	}

	public List<Spaceship> createSpaceships(Iterator<BaseEntity<Key>> entities) {
		List<Spaceship> result = new ArrayList<Spaceship>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createSpaceship(entity));
		}
		return result;
	}

	public CrewMember createCrewMember(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		CrewMember result = new CrewMember();
		result.setKey(DbEntity.CrewMember.createRestKey(entity.getKey()));
		result.setFirstName(wrapper.getString(DbCrewMemberField.FirstName));
		result.setLastName(wrapper.getString(DbCrewMemberField.LastName));
		return result;
	}

	public List<CrewMember> createCrewMembers(Iterator<BaseEntity<Key>> entities) {
		List<CrewMember> result = new ArrayList<CrewMember>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createCrewMember(entity));
		}
		return result;
	}

	public List<Mission> createMissions(Iterator<Entity> dbMissions) {

		List<Mission> missions = new ArrayList<Mission>();
		while (dbMissions.hasNext()) {
			DbEntityWrapper dbMission = DbEntityWrapper.wrap(dbMissions.next());
			String jsonMission = dbMission.getString(DbMissionField.MissionDefinition);
			MissionDefinition missionDefinition = MissionDefinition.fromJson(jsonMission);

			List<MissionObjective> objectives = new ArrayList<MissionObjective>();
			for (ObjectiveDefinition objectiveDefinition : missionDefinition.getObjectives()) {
				MissionObjective objective = new MissionObjective();
				objective.setDescription(objectiveDefinition.toString());
				objectives.add(objective);
			}

			Mission mission = new Mission();
			mission.setKey(dbMission.getEntity().getKey().getName());
			mission.setStatus(dbMission.getString(DbMissionField.MissionStatus));
			mission.setObjectives(objectives);

			missions.add(mission);
		}

		return missions;
	}

	public List<MissionState> createMissionStates(Iterator<Entity> dbMissionStates) {

		List<MissionState> missionStates = new ArrayList<MissionState>();
		while (dbMissionStates.hasNext()) {
			DbEntityWrapper dbMissionState = DbEntityWrapper.wrap(dbMissionStates.next());

			MissionState missionState = new MissionState();
			missionState.setKey(dbMissionState.getEntity().getKey().getName());
			missionState.setTimestamp(dbMissionState.getInstant(DbMissionStateField.Timestamp));
			missionState.setObjectiveIndex(dbMissionState.getLong(DbMissionStateField.ObjectiveIndex));
			missionState.setStartTimestamp(dbMissionState.getInstant(DbMissionStateField.StartTimestamp));
			missionState.setEndTimestamp(dbMissionState.getInstant(DbMissionStateField.EndTimestamp));

			missionStates.add(missionState);
		}
		return missionStates;
	}

	public List<SpaceshipState> createSpaceshipStates(Iterator<Entity> dbSpaceshipStates) {

		List<SpaceshipState> spaceshipStates = new ArrayList<SpaceshipState>();
		while (dbSpaceshipStates.hasNext()) {
			DbEntityWrapper dbSpaceshipState = DbEntityWrapper.wrap(dbSpaceshipStates.next());

			Key dbLocationKey = dbSpaceshipState.getKey(DbSpaceshipStateField.LocationKey);
			Key dbDestinationKey = dbSpaceshipState.getKey(DbSpaceshipStateField.DestinationKey);

			SpaceshipState spaceshipState = new SpaceshipState();
			spaceshipState.setKey(dbSpaceshipState.getEntity().getKey().getName());
			spaceshipState.setTimestamp(dbSpaceshipState.getInstant(DbSpaceshipStateField.Timestamp));
			spaceshipState.setLocationX(dbSpaceshipState.getDouble(DbSpaceshipStateField.LocationX));
			spaceshipState.setLocationY(dbSpaceshipState.getDouble(DbSpaceshipStateField.LocationY));
			spaceshipState.setLocationZ(dbSpaceshipState.getDouble(DbSpaceshipStateField.LocationZ));
			if (dbLocationKey != null) {
				spaceshipState.setLocationKeyKind(dbLocationKey.getKind());
				spaceshipState.setLocationKeyValue(getKeyValue(dbLocationKey));
			}
			spaceshipState.setLocationArrival(dbSpaceshipState.getInstant(DbSpaceshipStateField.LocationArrival));
			spaceshipState.setSpeed(dbSpaceshipState.getDouble(DbSpaceshipStateField.Speed));
			spaceshipState.setDestinationX(dbSpaceshipState.getDouble(DbSpaceshipStateField.DestinationX));
			spaceshipState.setDestinationY(dbSpaceshipState.getDouble(DbSpaceshipStateField.DestinationY));
			spaceshipState.setDestinationZ(dbSpaceshipState.getDouble(DbSpaceshipStateField.DestinationZ));
			if (dbDestinationKey != null) {
				spaceshipState.setDestinationKeyKind(dbDestinationKey.getKind());
				spaceshipState.setDestinationKeyValue(getKeyValue(dbDestinationKey));
			}
			spaceshipStates.add(spaceshipState);
		}

		return spaceshipStates;
	}

	public List<CrewMemberState> createCrewMemberStates(Iterator<Entity> dbCrewMemberStates) {

		List<CrewMemberState> crewMemberStates = new ArrayList<CrewMemberState>();
		while (dbCrewMemberStates.hasNext()) {
			DbEntityWrapper dbCrewMemberState = DbEntityWrapper.wrap(dbCrewMemberStates.next());

			Key dbLocationKey = dbCrewMemberState.getKey(DbCrewMemberStateField.LocationKey);
			Key dbDestinationKey = dbCrewMemberState.getKey(DbCrewMemberStateField.DestinationKey);

			CrewMemberState crewMemberState = new CrewMemberState();
			crewMemberState.setKey(dbCrewMemberState.getEntity().getKey().getName());
			crewMemberState.setTimestamp(dbCrewMemberState.getInstant(DbCrewMemberStateField.Timestamp));
			if (dbLocationKey != null) {
				crewMemberState.setLocationKeyKind(dbLocationKey.getKind());
				crewMemberState.setLocationKeyValue(getKeyValue(dbLocationKey));
			}
			crewMemberState.setLocationArrival(dbCrewMemberState.getInstant(DbCrewMemberStateField.LocationArrival));
			if (dbDestinationKey != null) {
				crewMemberState.setDestinationKeyKind(dbDestinationKey.getKind());
				crewMemberState.setDestinationKeyValue(getKeyValue(dbDestinationKey));
			}
			crewMemberStates.add(crewMemberState);
		}

		return crewMemberStates;
	}

	private String getKeyValue(Key dbKey) {
		if (dbKey.hasId()) {
			return dbKey.getId().toString();
		}
		if (dbKey.hasName()) {
			return dbKey.getName();
		}
		return null;
	}

}
