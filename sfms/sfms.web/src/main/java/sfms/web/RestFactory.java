package sfms.web;

import java.util.ArrayList;
import java.util.List;

import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.SpaceshipModel;

public class RestFactory {

	public Spaceship createSpaceship(SpaceshipModel spaceshipModel) {
		Spaceship result = new Spaceship();
		result.setId(asLong(spaceshipModel.getId()));
		result.setName(spaceshipModel.getName());
		return result;
	}

	public List<Spaceship> createSpaceships(Iterable<SpaceshipModel> spaceshipModels) {
		List<Spaceship> result = new ArrayList<Spaceship>();
		for (SpaceshipModel spaceshipModel : spaceshipModels) {
			result.add(createSpaceship(spaceshipModel));
		}
		return result;
	}

	public CrewMember createCrewMember(CrewMemberModel crewMemberModel) {
		CrewMember result = new CrewMember();
		result.setId(asLong(crewMemberModel.getId()));
		result.setFirstName(crewMemberModel.getFirstName());
		result.setLastName(crewMemberModel.getLastName());
		return result;
	}

	public List<CrewMember> createCrewMembers(Iterable<CrewMemberModel> crewMemberModels) {
		List<CrewMember> result = new ArrayList<CrewMember>();
		for (CrewMemberModel crewMemberModel : crewMemberModels) {
			result.add(createCrewMember(crewMemberModel));
		}
		return result;
	}

	private Long asLong(String value) {
		if (value == null)
			return null;
		return Long.valueOf(value);
	}

}
