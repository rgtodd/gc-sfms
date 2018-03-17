package sfms.rest;

import java.util.ArrayList;
import java.util.List;

import sfms.db.DbCrewMember;
import sfms.db.DbSpaceship;
import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;

public class RestFactory {

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
