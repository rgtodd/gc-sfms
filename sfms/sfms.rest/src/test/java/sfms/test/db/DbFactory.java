package sfms.test.db;

import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;

public class DbFactory {

	public DbSpaceship createSpaceship(Spaceship spaceship) {
		DbSpaceship result = new DbSpaceship(spaceship.getKey());
		result.setName(spaceship.getName());
		return result;
	}

	public DbCrewMember createCrewMember(CrewMember crewMember) {
		DbCrewMember result = new DbCrewMember(crewMember.getKey());
		result.setFirstName(crewMember.getFirstName());
		result.setLastName(crewMember.getLastName());
		return result;
	}

}
