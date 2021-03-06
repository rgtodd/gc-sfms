package sfms.rest.controllers;

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
import sfms.db.DbFieldSchema;
import sfms.db.DbValueFactory;
import sfms.db.schemas.DbCrewMemberField;
import sfms.db.schemas.DbEntity;
import sfms.rest.RestFactory;
import sfms.rest.Throttle;
import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.CrewMemberState;
import sfms.rest.api.models.Mission;
import sfms.rest.api.schemas.CrewMemberField;
import sfms.rest.db.RestQuery;
import sfms.rest.db.RestQueryBuilder;
import sfms.rest.db.RestQueryResults;

/**
 * Controller for the Crew Member REST service.
 * 
 * Provides basic CRUD operations for Crew Member entities.
 * 
 */
@RestController
@RequestMapping("/crewMember")
public class CrewMemberRestController {

	private static final Map<String, DbFieldSchema> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, DbFieldSchema>();
		s_dbFieldMap.put(CrewMemberField.FirstName.getName(), DbCrewMemberField.FirstName);
		s_dbFieldMap.put(CrewMemberField.LastName.getName(), DbCrewMemberField.LastName);
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int MAX_PAGE_SIZE = 100000;

	@Autowired
	private Throttle m_throttle;

	@GetMapping(value = "/{key}")
	public CrewMember getLookup(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbCrewMemberKey = DbEntity.CrewMember.createEntityKey(datastore, key);

		Entity dbCrewMember = datastore.get(dbCrewMemberKey);

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(dbCrewMember);

		crewMember.setMissions(getMissions(datastore, dbCrewMemberKey.getId(), factory));
		crewMember.setStates(getCrewMemberStates(datastore, dbCrewMemberKey.getId(), factory));

		return crewMember;
	}

	@GetMapping(value = "")
	public SearchResult<CrewMember> getSearch(
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

		RestQuery dbCrewMemberQuery = RestQueryBuilder.newEntityQueryBuilder(s_dbFieldMap)
				.setKind(DbEntity.CrewMember.getKind())
				.setLimit(limit)
				.addSortCriteria(sort)
				.setFilterCriteria(datastore, filter)
				.setStartCursor(bookmark)
				.build();

		RestQueryResults dbCrewMembers = dbCrewMemberQuery.run(datastore);

		RestFactory factory = new RestFactory();
		List<CrewMember> crewMembers = factory.createCrewMembers(dbCrewMembers);

		SearchResult<CrewMember> result = new SearchResult<CrewMember>();
		result.setEntities(crewMembers);
		result.setEndingBookmark(dbCrewMembers.getCursorAfter().toUrlSafe());
		result.setEndOfResults(crewMembers.size() < limit);

		return result;
	}

	@PutMapping(value = "/{key}")
	public UpdateResult<String> putUpdate(@PathVariable String key, @RequestBody CrewMember crewMember)
			throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbCrewMemberKey = DbEntity.CrewMember.createEntityKey(datastore, key);

		Entity dbCrewMember = Entity.newBuilder(dbCrewMemberKey)
				.set(DbCrewMemberField.FirstName.getName(), DbValueFactory.asValue(crewMember.getFirstName()))
				.set(DbCrewMemberField.LastName.getName(), DbValueFactory.asValue(crewMember.getLastName()))
				.build();

		datastore.update(dbCrewMember);

		UpdateResult<String> result = new UpdateResult<String>();
		result.setKey(key);

		return result;
	}

	@PostMapping(value = "")
	public CreateResult<String> postCreate(@RequestBody CrewMember crewMember) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		IncompleteKey dbCrewMemberIncompleteKey = datastore.newKeyFactory().setKind(DbEntity.CrewMember.getKind())
				.newKey();

		FullEntity<IncompleteKey> dbCrewMember = FullEntity.newBuilder(dbCrewMemberIncompleteKey)
				.set(DbCrewMemberField.FirstName.getName(), DbValueFactory.asValue(crewMember.getFirstName()))
				.set(DbCrewMemberField.LastName.getName(), DbValueFactory.asValue(crewMember.getLastName()))
				.build();

		Key dbCrewMemberKey = datastore.put(dbCrewMember).getKey();

		CreateResult<String> result = new CreateResult<String>();
		result.setKey(DbEntity.CrewMember.createRestKey(dbCrewMemberKey));

		return result;
	}

	@DeleteMapping(value = "/{key}")
	public DeleteResult<String> delete(@PathVariable String key) throws Exception {

		if (!m_throttle.increment()) {
			throw new Exception("Function is throttled.");
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Key dbCrewMemberKey = DbEntity.CrewMember.createEntityKey(datastore, key);

		datastore.delete(dbCrewMemberKey);

		DeleteResult<String> result = new DeleteResult<String>();
		result.setKey(DbEntity.CrewMember.createRestKey(dbCrewMemberKey));

		return result;
	}

	private List<Mission> getMissions(Datastore datastore, Long crewMemberId, RestFactory factory) {

		String missionKeyPrefix = CompositeKeyBuilder.create()
				.append(DbEntity.CrewMember.getKind())
				.append(crewMemberId)
				.build()
				.toString();

		Iterator<Entity> dbMissions = Db.getEntities(datastore, DbEntity.Mission.getKind(), missionKeyPrefix);

		String missionStateKeyPrefix = CompositeKeyBuilder.create()
				.append(DbEntity.CrewMember.getKind())
				.append(crewMemberId)
				.build()
				.toString();

		Iterator<Entity> dbMissionStates = Db.getEntities(datastore, DbEntity.MissionState.getKind(),
				missionStateKeyPrefix);

		return factory.createMissions(dbMissions, dbMissionStates);
	}

	private List<CrewMemberState> getCrewMemberStates(Datastore datastore, Long crewMemberId, RestFactory factory) {

		String keyPrefix = CompositeKeyBuilder.create()
				.append(crewMemberId)
				.build()
				.toString();

		Iterator<Entity> dbCrewMemberStates = Db.getEntities(datastore, DbEntity.CrewMemberState.getKind(), keyPrefix);

		return factory.createCrewMemberStates(dbCrewMemberStates);
	}
}
