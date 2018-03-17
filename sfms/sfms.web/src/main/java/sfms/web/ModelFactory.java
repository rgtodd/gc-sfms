package sfms.web;

import java.util.ArrayList;
import java.util.List;

import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.SpaceshipModel;

public class ModelFactory {

	public SpaceshipModel createSpaceship(Spaceship spaceship) {
		SpaceshipModel result = new SpaceshipModel();
		result.setId(asString(spaceship.getId()));
		result.setName(spaceship.getName());
		return result;
	}

	public List<SpaceshipModel> createSpaceships(Iterable<Spaceship> spaceships) {
		List<SpaceshipModel> result = new ArrayList<SpaceshipModel>();
		for (Spaceship spaceship : spaceships) {
			result.add(createSpaceship(spaceship));
		}
		return result;
	}

	public CrewMemberModel createCrewMember(CrewMember crewMember) {
		CrewMemberModel result = new CrewMemberModel();
		result.setId(asString(crewMember.getId()));
		result.setFirstName(crewMember.getFirstName());
		result.setLastName(crewMember.getLastName());
		return result;
	}

	public List<CrewMemberModel> createCrewMembers(Iterable<CrewMember> crewMembers) {
		List<CrewMemberModel> result = new ArrayList<CrewMemberModel>();
		for (CrewMember crewMember : crewMembers) {
			result.add(createCrewMember(crewMember));
		}
		return result;
	}

	private String asString(Long value) {
		if (value == null)
			return null;
		return value.toString();
	}

}
