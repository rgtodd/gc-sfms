package sfms.web;

import java.util.ArrayList;
import java.util.List;

import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.web.models.ClusterModel;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.SectorModel;
import sfms.web.models.SpaceshipModel;
import sfms.web.models.StarModel;

public class RestFactory {

	public Cluster createCluster(ClusterModel clusterModel) {
		Cluster result = new Cluster();
		result.setKey(clusterModel.getKey());
		result.setMinimumX(clusterModel.getMinimumX());
		result.setMinimumY(clusterModel.getMinimumY());
		result.setMinimumZ(clusterModel.getMinimumZ());
		result.setMaximumX(clusterModel.getMaximumX());
		result.setMaximumY(clusterModel.getMaximumY());
		result.setMaximumZ(clusterModel.getMaximumZ());
		return result;
	}

	public List<Cluster> createClusters(Iterable<ClusterModel> clusterModels) {
		List<Cluster> result = new ArrayList<Cluster>();
		for (ClusterModel clusterModel : clusterModels) {
			result.add(createCluster(clusterModel));
		}
		return result;
	}

	public CrewMember createCrewMember(CrewMemberModel crewMemberModel) {
		CrewMember result = new CrewMember();
		result.setKey(crewMemberModel.getKey());
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

	public Sector createSector(SectorModel sectorModel) {
		Sector result = new Sector();
		result.setKey(sectorModel.getKey());
		result.setMinimumX(sectorModel.getMinimumX());
		result.setMinimumY(sectorModel.getMinimumY());
		result.setMinimumZ(sectorModel.getMinimumZ());
		result.setMaximumX(sectorModel.getMaximumX());
		result.setMaximumY(sectorModel.getMaximumY());
		result.setMaximumZ(sectorModel.getMaximumZ());
		return result;
	}

	public List<Sector> createSectors(Iterable<SectorModel> sectorModels) {
		List<Sector> result = new ArrayList<Sector>();
		for (SectorModel sectorModel : sectorModels) {
			result.add(createSector(sectorModel));
		}
		return result;
	}

	public Spaceship createSpaceship(SpaceshipModel spaceshipModel) {
		Spaceship result = new Spaceship();
		result.setKey(spaceshipModel.getKey());
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
		result.setClusterKey(starModel.getClusterKey());
		result.setSectorKey(starModel.getSectorKey());
		result.setHipparcosId(starModel.getHipparcosId());
		result.setHenryDraperId(starModel.getHenryDraperId());
		result.setHarvardRevisedId(starModel.getHarvardRevisedId());
		result.setGlieseId(starModel.getGlieseId());
		result.setBayerFlamsteedId(starModel.getBayerFlamsteedId());
		result.setProperName(starModel.getProperName());
		result.setRightAscension(starModel.getRightAscension());
		result.setDeclination(starModel.getDeclination());
		result.setDistance(starModel.getDistance());
		result.setProperMotionRightAscension(starModel.getProperMotionRightAscension());
		result.setProperMotionDeclination(starModel.getProperMotionDeclination());
		result.setRadialVelocity(starModel.getRadialVelocity());
		result.setMagnitude(starModel.getMagnitude());
		result.setAbsoluteMagnitude(starModel.getAbsoluteMagnitude());
		result.setSpectrum(starModel.getSpectrum());
		result.setColorIndex(starModel.getColorIndex());
		result.setX(starModel.getX());
		result.setY(starModel.getY());
		result.setZ(starModel.getZ());
		result.setVx(starModel.getVx());
		result.setVy(starModel.getVy());
		result.setVz(starModel.getVz());
		result.setRightAcensionRadians(starModel.getRightAcensionRadians());
		result.setDeclinationRadians(starModel.getDeclinationRadians());
		result.setProperMotionRightAscensionRadians(
				starModel.getProperMotionRightAscensionRadians());
		result.setProperMotionDeclinationRadians(starModel.getProperMotionDeclinationRadians());
		result.setBayerId(starModel.getBayerId());
		result.setFlamsteed(starModel.getFlamsteed());
		result.setConstellation(starModel.getConstellation());
		result.setCompanionStarId(starModel.getCompanionStarId());
		result.setPrimaryStarId(starModel.getPrimaryStarId());
		result.setMultipleStarId(starModel.getMultipleStarId());
		result.setLuminosity(starModel.getLuminosity());
		result.setVariableStarDesignation(starModel.getVariableStarDesignation());
		result.setVariableMinimum(starModel.getVariableMinimum());
		result.setVariableMaximum(starModel.getVariableMaximum());
		return result;
	}

	public List<Star> createStars(Iterable<StarModel> starModels) {
		List<Star> result = new ArrayList<Star>();
		for (StarModel starModel : starModels) {
			result.add(createStar(starModel));
		}
		return result;
	}
}
