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
import sfms.db.DbSpaceship;
import sfms.rest.CreateResult;
import sfms.rest.DbFactory;
import sfms.rest.DeleteResult;
import sfms.rest.RestFactory;
import sfms.rest.SearchResult;
import sfms.rest.UpdateResult;
import sfms.rest.models.Spaceship;

@RestController
@RequestMapping("/spaceship")
public class SpaceshipRestController {

	@GetMapping(value = "/{id}")
	public Spaceship get(@PathVariable Long id) throws Exception {

		DbSpaceship dbSpaceship = Database.INSTANCE.getSpaceships().get(id);
		if (dbSpaceship == null) {
			throw new Exception("Entity not found.");
		}

		RestFactory factory = new RestFactory();
		Spaceship result = factory.createSpaceship(dbSpaceship);

		return result;
	}

	@GetMapping(value = "")
	public SearchResult<Spaceship> search() throws Exception {

		RestFactory factory = new RestFactory();
		List<Spaceship> spaceships = new ArrayList<Spaceship>();
		for (DbSpaceship dbSpaceship : Database.INSTANCE.getSpaceships().values()) {
			spaceships.add(factory.createSpaceship(dbSpaceship));
		}

		SearchResult<Spaceship> result = new SearchResult<Spaceship>();
		result.setEntities(spaceships);

		return result;
	}

	@PostMapping(value = "/{id}")
	public UpdateResult<Long> update(@PathVariable long id, @RequestBody Spaceship spaceship) {

		spaceship.setId(id);

		DbFactory factory = new DbFactory();
		DbSpaceship dbSpaceship = factory.createSpaceship(spaceship);
		Database.INSTANCE.getSpaceships().put(dbSpaceship.getId(), dbSpaceship);

		UpdateResult<Long> result = new UpdateResult<Long>();
		result.setKey(dbSpaceship.getId());

		return result;
	}

	@PostMapping(value = "")
	public CreateResult<Long> create(@RequestBody Spaceship spaceship) {

		spaceship.setId(Database.INSTANCE.getNextId());

		DbFactory factory = new DbFactory();
		DbSpaceship dbSpaceship = factory.createSpaceship(spaceship);
		Database.INSTANCE.getSpaceships().put(dbSpaceship.getId(), dbSpaceship);

		CreateResult<Long> result = new CreateResult<Long>();
		result.setKey(dbSpaceship.getId());

		return result;
	}

	@DeleteMapping(value = "/{id}")
	public DeleteResult<Long> delete(@PathVariable long id) {

		Database.INSTANCE.getSpaceships().remove(id);

		DeleteResult<Long> result = new DeleteResult<Long>();
		result.setKey(id);

		return result;
	}

}
