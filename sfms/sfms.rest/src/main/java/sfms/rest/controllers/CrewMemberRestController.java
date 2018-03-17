package sfms.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sfms.db.Database;
import sfms.db.DbCrewMember;
import sfms.rest.CreateResult;
import sfms.rest.DbFactory;
import sfms.rest.DeleteResult;
import sfms.rest.RestFactory;
import sfms.rest.SearchResult;
import sfms.rest.UpdateResult;
import sfms.rest.models.CrewMember;

@RestController
@RequestMapping("/crewMember")
public class CrewMemberRestController {

	@GetMapping(value = "/{id}")
	public CrewMember get(@PathVariable Long id) throws Exception {

		DbCrewMember dbCrewMember = Database.INSTANCE.getCrewMembers().get(id);
		if (dbCrewMember == null) {
			throw new Exception("Entity not found.");
		}

		RestFactory factory = new RestFactory();
		CrewMember result = factory.createCrewMember(dbCrewMember);

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<CrewMember> search() throws Exception {

		RestFactory factory = new RestFactory();
		List<CrewMember> crewMembers = new ArrayList<CrewMember>();
		for (DbCrewMember dbCrewMember : Database.INSTANCE.getCrewMembers().values()) {
			crewMembers.add(factory.createCrewMember(dbCrewMember));
		}

		SearchResult<CrewMember> result = new SearchResult<CrewMember>();
		result.setEntities(crewMembers);

		return result;
	}

	@PostMapping(value = "/{id}")
	public UpdateResult<Long> update(@PathVariable long id, @RequestBody CrewMember crewMember) {

		crewMember.setId(id);

		DbFactory factory = new DbFactory();
		DbCrewMember dbCrewMember = factory.createCrewMember(crewMember);
		Database.INSTANCE.getCrewMembers().put(dbCrewMember.getId(), dbCrewMember);

		UpdateResult<Long> result = new UpdateResult<Long>();
		result.setKey(dbCrewMember.getId());

		return result;
	}

	@PostMapping(value = "")
	public CreateResult<Long> create(@RequestBody CrewMember crewMember) {

		crewMember.setId(Database.INSTANCE.getNextId());

		DbFactory factory = new DbFactory();
		DbCrewMember dbCrewMember = factory.createCrewMember(crewMember);
		Database.INSTANCE.getCrewMembers().put(dbCrewMember.getId(), dbCrewMember);

		CreateResult<Long> result = new CreateResult<Long>();
		result.setKey(dbCrewMember.getId());

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<Long> delete(@PathVariable long id) {

		Database.INSTANCE.getCrewMembers().remove(id);

		DeleteResult<Long> result = new DeleteResult<Long>();
		result.setKey(id);

		return result;
	}

}