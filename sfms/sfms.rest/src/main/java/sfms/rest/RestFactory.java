package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.cloud.datastore.Entity;

import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.rest.db.schemas.DbCrewMemberField;
import sfms.rest.db.schemas.DbSpaceshipField;
import sfms.rest.db.schemas.DbStarField;

public class RestFactory {

	public Star createStar(Entity entity) {
		Star result = new Star();
		result.setKey(entity.getKey().getId().toString());
		result.setStarId(entity.getString(DbStarField.StarId.getName()));
		result.setProperName(entity.getString(DbStarField.ProperName.getName()));
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

	public Spaceship createSpaceship(Entity entity) {
		Spaceship result = new Spaceship();
		result.setKey(entity.getKey().getId().toString());
		result.setName(entity.getString(DbSpaceshipField.Name.getName()));
		return result;
	}

	public List<Spaceship> createSpaceships(Iterator<Entity> entities) {
		List<Spaceship> result = new ArrayList<Spaceship>();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			result.add(createSpaceship(entity));
		}
		return result;
	}

	public CrewMember createCrewMember(Entity entity) {
		CrewMember result = new CrewMember();
		result.setKey(entity.getKey().getId().toString());
		result.setFirstName(entity.getString(DbCrewMemberField.FirstName.getName()));
		result.setLastName(entity.getString(DbCrewMemberField.LastName.getName()));
		return result;
	}

	public List<CrewMember> createCrewMembers(Iterator<Entity> entities) {
		List<CrewMember> result = new ArrayList<CrewMember>();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			result.add(createCrewMember(entity));
		}
		return result;
	}

}
