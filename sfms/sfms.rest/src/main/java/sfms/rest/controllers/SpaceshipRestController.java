package sfms.rest.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;

import sfms.db.CompositeKeyBuilder;
import sfms.db.Db;
import sfms.db.DbEntityWrapper;
import sfms.db.DbFieldSchema;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbMissionField;
import sfms.db.schemas.DbMissionStateField;
import sfms.db.schemas.DbSpaceshipField;
import sfms.db.schemas.DbSpaceshipStateField;
import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Mission;
import sfms.rest.api.models.MissionObjective;
import sfms.rest.api.models.MissionState;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.SpaceshipState;
import sfms.rest.api.schemas.SpaceshipField;
import sfms.rest.db.RestQuery;
import sfms.rest.db.RestQueryBuilder;
import sfms.rest.db.RestQueryResults;
import sfms.simulator.json.MissionDefinition;
import sfms.simulator.json.ObjectiveDefinition;

/**
 * Controller for the Spaceship REST service.
 * 
 * Provides basic CRUD operations for Space Ship entities.
 * 
 */
@RestController
@RequestMapping("/spaceship")
public class SpaceshipRestController {

	private static final Map<String, DbFieldSchema> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, DbFieldSchema>();
		s_dbFieldMap.put(SpaceshipField.Name.getName(), DbSpaceshipField.Name);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{key}")
	public Spaceship getLookup(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSpaceshipKey = DbEntity.Spaceship.createEntityKey(datastore, key);

		Entity dbSpaceship = datastore.get(dbSpaceshipKey);

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(dbSpaceship);

		spaceship.setMissions(getMissions(datastore, dbSpaceshipKey.getId()));
		spaceship.setMissionStates(getMissionStates(datastore, dbSpaceshipKey.getId()));
		spaceship.setStates(getSpaceshipStates(datastore, dbSpaceshipKey.getId()));

		return spaceship;
	}

	@GetMapping(value = "")
	public SearchResult<Spaceship> getSearch(
			@RequestParam(RestParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(RestParameters.PAGE_INDEX) Optional<Long> pageIndex,
			@RequestParam(RestParameters.PAGE_SIZE) Optional<Integer> pageSize,
			@RequestParam(RestParameters.FILTER) Optional<String> filter,
			@RequestParam(RestParameters.SORT) Optional<String> sort,
			@RequestParam(RestParameters.DETAIL) Optional<String> detail) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		int limit = Integer.min(pageSize.orElse(DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		RestQuery dbSpaceshipQuery = RestQueryBuilder.newEntityQueryBuilder(s_dbFieldMap)
				.setKind(DbEntity.Spaceship.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setFilterCriteria(datastore, filter)
				.setStartCursor(bookmark)
				.build();

		RestQueryResults dbSpaceships = dbSpaceshipQuery.run(datastore);

		RestFactory factory = new RestFactory();
		List<Spaceship> spaceships = factory.createSpaceships(dbSpaceships);

		SearchResult<Spaceship> result = new SearchResult<Spaceship>();
		result.setEntities(spaceships);
		result.setEndingBookmark(dbSpaceships.getCursorAfter().toUrlSafe());
		result.setEndOfResults(spaceships.size() < limit);

		return result;
	}

	@PutMapping(value = "/{key}")
	public UpdateResult<String> putUpdate(@PathVariable String key, @RequestBody Spaceship spaceship) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSpaceshipKey = DbEntity.Spaceship.createEntityKey(datastore, key);

		Entity dbSpaceship = Entity.newBuilder(dbSpaceshipKey)
				.set(DbSpaceshipField.Name.getName(), DbValueFactory.asValue(spaceship.getName()))
				.build();

		datastore.update(dbSpaceship);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(DbEntity.Spaceship.createRestKey(dbSpaceshipKey));

		return result;
	}

	@PostMapping(value = "")
	public CreateResult<String> postCreate(@RequestBody Spaceship spaceship) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		IncompleteKey dbSpaceshipIncompleteKey = datastore.newKeyFactory().setKind(DbEntity.Spaceship.getKind())
				.newKey();

		FullEntity<IncompleteKey> dbSpaceship = FullEntity.newBuilder(dbSpaceshipIncompleteKey)
				.set(DbSpaceshipField.Name.getName(), DbValueFactory.asValue(spaceship.getName()))
				.build();

		Key dbSpaceshipKey = datastore.put(dbSpaceship).getKey();

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.Spaceship.createRestKey(dbSpaceshipKey));

		return result;
	}

	@DeleteMapping(value = "/{key}")
	public DeleteResult<String> delete(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbSpaceshipKey = DbEntity.Spaceship.createEntityKey(datastore, key);

		datastore.delete(dbSpaceshipKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.Spaceship.createRestKey(dbSpaceshipKey));

		return result;
	}

	private List<Mission> getMissions(Datastore datastore, Long shipId) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(DbEntity.Spaceship.getKind())
				.append(shipId)
				.build()
				.toString();

		Iterator<Entity> dbMissions = Db.getEntities(datastore, DbEntity.Mission.getKind(), keyPrefix);

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

	private List<MissionState> getMissionStates(Datastore datastore, Long shipId) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(DbEntity.Spaceship.getKind())
				.append(shipId)
				.build()
				.toString();

		Iterator<Entity> dbMissionStates = Db.getEntities(datastore, DbEntity.MissionState.getKind(), keyPrefix);

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

	private List<SpaceshipState> getSpaceshipStates(Datastore datastore, Long shipId) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(shipId)
				.build()
				.toString();

		Iterator<Entity> dbSpaceshipStates = Db.getEntities(datastore, DbEntity.SpaceshipState.getKind(), keyPrefix);

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
