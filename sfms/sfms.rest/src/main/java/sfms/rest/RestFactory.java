package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.cloud.datastore.Entity;

import sfms.db.DbCrewMember;
import sfms.db.DbSpaceship;
import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;
import sfms.rest.models.Star;
import sfms.rest.schemas.StarEntitySchema;

public class RestFactory {

	public Star createStar(Entity entity) {
		Star result = new Star();
		result.setKey(entity.getKey().getId().toString());
		result.setStarId(entity.getString(StarEntitySchema.StarId));
		result.setProperName(entity.getString(StarEntitySchema.ProperName));
		return result;
	}

	public List<Star> createStars(Iterator<Entity> entities) {
		List<Star> result = new ArrayList<Star>();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			result.add(createStar(entity));
		}
		return result;
	}

	public Spaceship createSpaceship(DbSpaceship dbSpaceship) {
		Spaceship result = new Spaceship();
		result.setId(dbSpaceship.getId());
		result.setName(dbSpaceship.getName());
		return result;
	}

	public List<Spaceship> createSpaceships(Iterable<DbSpaceship> dbSpaceships) {
		List<Spaceship> result = new ArrayList<Spaceship>();
		for (DbSpaceship dbSpaceship : dbSpaceships) {
			result.add(createSpaceship(dbSpaceship));
		}
		return result;
	}

	public CrewMember createCrewMember(DbCrewMember dbCrewMember) {
		CrewMember result = new CrewMember();
		result.setId(dbCrewMember.getId());
		result.setFirstName(dbCrewMember.getFirstName());
		result.setLastName(dbCrewMember.getLastName());
		return result;
	}

	public List<CrewMember> createCrewMembers(Iterable<DbCrewMember> dbCrewMembers) {
		List<CrewMember> result = new ArrayList<CrewMember>();
		for (DbCrewMember dbCrewMember : dbCrewMembers) {
			result.add(createCrewMember(dbCrewMember));
		}
		return result;
	}

}
