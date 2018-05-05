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

public class ModelFactory {

	public ClusterModel createCluster() {
		ClusterModel result = new ClusterModel();
		return result;
	}

	public ClusterModel createCluster(Cluster cluster) {
		ClusterModel result = new ClusterModel();
		result.setKey(cluster.getKey());
		result.setClusterPartition(cluster.getClusterPartition());
		result.setClusterX(cluster.getClusterX());
		result.setClusterY(cluster.getClusterY());
		result.setClusterZ(cluster.getClusterZ());
		result.setMinimumX(cluster.getMinimumX());
		result.setMinimumY(cluster.getMinimumY());
		result.setMinimumZ(cluster.getMinimumZ());
		result.setMaximumX(cluster.getMaximumX());
		result.setMaximumY(cluster.getMaximumY());
		result.setMaximumZ(cluster.getMaximumZ());
		result.setStars(createStars(cluster.getStars()));
		return result;
	}

	public List<ClusterModel> createClusters(Iterable<Cluster> clusters) {
		List<ClusterModel> result = new ArrayList<ClusterModel>();
		for (Cluster cluster : clusters) {
			result.add(createCluster(cluster));
		}
		return result;
	}

	public CrewMemberModel createCrewMember() {
		CrewMemberModel result = new CrewMemberModel();
		return result;
	}

	public CrewMemberModel createCrewMember(CrewMember crewMember) {
		CrewMemberModel result = new CrewMemberModel();
		result.setKey(crewMember.getKey());
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

	public SectorModel createSector() {
		SectorModel result = new SectorModel();
		return result;
	}

	public SectorModel createSector(Sector sector) {
		SectorModel result = new SectorModel();
		result.setKey(sector.getKey());
		result.setSectorX(sector.getSectorX());
		result.setSectorY(sector.getSectorY());
		result.setSectorZ(sector.getSectorZ());
		result.setMinimumX(sector.getMinimumX());
		result.setMinimumY(sector.getMinimumY());
		result.setMinimumZ(sector.getMinimumZ());
		result.setMaximumX(sector.getMaximumX());
		result.setMaximumY(sector.getMaximumY());
		result.setMaximumZ(sector.getMaximumZ());
		result.setStars(createStars(sector.getStars()));
		return result;
	}

	public List<SectorModel> createSectors(Iterable<Sector> sectors) {
		List<SectorModel> result = new ArrayList<SectorModel>();
		for (Sector sector : sectors) {
			result.add(createSector(sector));
		}
		return result;
	}

	public SpaceshipModel createSpaceship() {
		SpaceshipModel result = new SpaceshipModel();
		return result;
	}

	public SpaceshipModel createSpaceship(Spaceship spaceship) {
		SpaceshipModel result = new SpaceshipModel();
		result.setKey(spaceship.getKey());
		result.setName(spaceship.getName());
		result.setX(spaceship.getX());
		result.setY(spaceship.getY());
		result.setZ(spaceship.getZ());
		result.setStarKey(spaceship.getStarKey());
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
		result.setCatalogId(star.getCatalogId());
		result.setClusterKey(star.getClusterKey());
		result.setSectorKey(star.getSectorKey());
		result.setHipparcosId(star.getHipparcosId());
		result.setHenryDraperId(star.getHenryDraperId());
		result.setHarvardRevisedId(star.getHarvardRevisedId());
		result.setGlieseId(star.getGlieseId());
		result.setBayerFlamsteedId(star.getBayerFlamsteedId());
		result.setProperName(star.getProperName());
		result.setRightAscension(star.getRightAscension());
		result.setDeclination(star.getDeclination());
		result.setDistance(star.getDistance());
		result.setProperMotionRightAscension(star.getProperMotionRightAscension());
		result.setProperMotionDeclination(star.getProperMotionDeclination());
		result.setRadialVelocity(star.getRadialVelocity());
		result.setMagnitude(star.getMagnitude());
		result.setAbsoluteMagnitude(star.getAbsoluteMagnitude());
		result.setSpectrum(star.getSpectrum());
		result.setColorIndex(star.getColorIndex());
		result.setX(star.getX());
		result.setY(star.getY());
		result.setZ(star.getZ());
		result.setVX(star.getVX());
		result.setVY(star.getVY());
		result.setVZ(star.getVZ());
		result.setRightAcensionRadians(star.getRightAcensionRadians());
		result.setDeclinationRadians(star.getDeclinationRadians());
		result.setProperMotionRightAscensionRadians(star.getProperMotionRightAscensionRadians());
		result.setProperMotionDeclinationRadians(star.getProperMotionDeclinationRadians());
		result.setBayerId(star.getBayerId());
		result.setFlamsteed(star.getFlamsteed());
		result.setConstellation(star.getConstellation());
		result.setCompanionStarId(star.getCompanionStarId());
		result.setPrimaryStarId(star.getPrimaryStarId());
		result.setMultipleStarId(star.getMultipleStarId());
		result.setLuminosity(star.getLuminosity());
		result.setVariableStarDesignation(star.getVariableStarDesignation());
		result.setVariableMinimum(star.getVariableMinimum());
		result.setVariableMaximum(star.getVariableMaximum());
		return result;
	}

	public List<StarModel> createStars(Iterable<Star> stars) {
		List<StarModel> result = new ArrayList<StarModel>();
		if (stars != null) {
			for (Star star : stars) {
				result.add(createStar(star));
			}
		}
		return result;
	}
}
