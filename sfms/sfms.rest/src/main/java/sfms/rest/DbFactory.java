package sfms.rest;

import sfms.db.DbCrewMember;
import sfms.db.DbSpaceship;
import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;

public class DbFactory {

	public DbSpaceship createSpaceship(Spaceship spaceship) {
		DbSpaceship result = new DbSpaceship(spaceship.getId());
		result.setName(spaceship.getName());
		return result;
	}

	public DbCrewMember createCrewMember(CrewMember crewMember) {
		DbCrewMember result = new DbCrewMember(crewMember.getId());
		result.setFirstName(crewMember.getFirstName());
		result.setLastName(crewMember.getLastName());
		return result;
	}

}
