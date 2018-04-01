package sfms.web;

import java.util.ArrayList;
import java.util.List;

import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.SpaceshipModel;
import sfms.web.models.StarModel;

public class RestFactory {

	public Spaceship createSpaceship(SpaceshipModel spaceshipModel) {
		Spaceship result = new Spaceship();
		result.setKey(spaceshipModel.getId());
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

	public Star createStar(StarModel starModel) {
		Star result = new Star();
		result.setKey(starModel.getKey());
		result.setStarId(starModel.getStarId());
		result.setProperName(starModel.getProperName());
		return result;
	}

	public List<Star> createStars(Iterable<StarModel> starModels) {
		List<Star> result = new ArrayList<Star>();
		for (StarModel starModel : starModels) {
			result.add(createStar(starModel));
		}
		return result;
	}

	public CrewMember createCrewMember(CrewMemberModel crewMemberModel) {
		CrewMember result = new CrewMember();
		result.setKey(crewMemberModel.getId());
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
}
