package sfms.web;

import java.util.ArrayList;
import java.util.List;

import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.SpaceshipModel;
import sfms.web.models.StarModel;

public class ModelFactory {

	public SpaceshipModel createSpaceship() {
		SpaceshipModel result = new SpaceshipModel();
		return result;
	}

	public SpaceshipModel createSpaceship(Spaceship spaceship) {
		SpaceshipModel result = new SpaceshipModel();
		result.setId(spaceship.getKey());
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

	public StarModel createStar() {
		StarModel result = new StarModel();
		return result;
	}

	public StarModel createStar(Star star) {
		StarModel result = new StarModel();
		result.setKey(star.getKey());
		result.setStarId(star.getStarId());
		result.setProperName(star.getProperName());
		return result;
	}

	public List<StarModel> createStars(Iterable<Star> stars) {
		List<StarModel> result = new ArrayList<StarModel>();
		for (Star star : stars) {
			result.add(createStar(star));
		}
		return result;
	}

	public CrewMemberModel createCrewMember() {
		CrewMemberModel result = new CrewMemberModel();
		return result;
	}

	public CrewMemberModel createCrewMember(CrewMember crewMember) {
		CrewMemberModel result = new CrewMemberModel();
		result.setId(crewMember.getKey());
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
}
