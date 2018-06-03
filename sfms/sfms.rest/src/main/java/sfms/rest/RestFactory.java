package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Key;

import sfms.db.DbEntityWrapper;
import sfms.db.schemas.DbClusterField;
import sfms.db.schemas.DbCrewMemberField;
import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSectorField;
import sfms.db.schemas.DbSpaceshipField;
import sfms.db.schemas.DbStarField;
import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;

/**
 * Factory used to create REST entities from data store entities.
 * 
 */
public class RestFactory {

	private final Logger logger = Logger.getLogger(RestFactory.class.getName());

	public Cluster createCluster(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Cluster result = new Cluster();
		result.setKey(DbEntity.Cluster.createRestKey(entity.getKey()));
		result.setClusterPartition(wrapper.getLong(DbClusterField.ClusterPartition));
		result.setClusterX(wrapper.getLong(DbClusterField.ClusterX));
		result.setClusterY(wrapper.getLong(DbClusterField.ClusterY));
		result.setClusterZ(wrapper.getLong(DbClusterField.ClusterZ));
		result.setMinimumX(wrapper.getLong(DbClusterField.MinimumX));
		result.setMinimumY(wrapper.getLong(DbClusterField.MinimumY));
		result.setMinimumZ(wrapper.getLong(DbClusterField.MinimumZ));
		result.setMaximumX(wrapper.getLong(DbClusterField.MaximumX));
		result.setMaximumY(wrapper.getLong(DbClusterField.MaximumY));
		result.setMaximumZ(wrapper.getLong(DbClusterField.MaximumZ));
		return result;
	}

	public List<Cluster> createClusters(Iterator<BaseEntity<Key>> entities) {
		List<Cluster> result = new ArrayList<Cluster>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createCluster(entity));
		}
		return result;
	}

	public Sector createSector(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Sector result = new Sector();
		result.setKey(DbEntity.Sector.createRestKey(entity.getKey()));
		result.setSectorX(wrapper.getLong(DbSectorField.SectorX));
		result.setSectorY(wrapper.getLong(DbSectorField.SectorY));
		result.setSectorZ(wrapper.getLong(DbSectorField.SectorZ));
		result.setMinimumX(wrapper.getLong(DbSectorField.MinimumX));
		result.setMinimumY(wrapper.getLong(DbSectorField.MinimumY));
		result.setMinimumZ(wrapper.getLong(DbSectorField.MinimumZ));
		result.setMaximumX(wrapper.getLong(DbSectorField.MaximumX));
		result.setMaximumY(wrapper.getLong(DbSectorField.MaximumY));
		result.setMaximumZ(wrapper.getLong(DbSectorField.MaximumZ));
		return result;
	}

	public List<Sector> createSectors(Iterator<BaseEntity<Key>> entities) {
		List<Sector> result = new ArrayList<Sector>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createSector(entity));
		}
		return result;
	}

	public Star createStar(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Star result = new Star();
		result.setKey(DbEntity.Star.createRestKey(entity.getKey()));
		result.setCatalogId(String.valueOf(wrapper.getLong(DbStarField.CatalogId)));
		result.setClusterKey(DbEntity.Cluster.createRestKey(wrapper.getKey(DbStarField.ClusterKey)));
		result.setSectorKey(DbEntity.Sector.createRestKey(wrapper.getKey(DbStarField.SectorKey)));
		result.setHipparcosId(wrapper.getString(DbStarField.HipparcosId));
		result.setHenryDraperId(wrapper.getString(DbStarField.HenryDraperId));
		result.setHarvardRevisedId(wrapper.getString(DbStarField.HarvardRevisedId));
		result.setGlieseId(wrapper.getString(DbStarField.GlieseId));
		result.setBayerFlamsteedId(wrapper.getString(DbStarField.BayerFlamsteedId));
		result.setProperName(wrapper.getString(DbStarField.ProperName));
		result.setRightAscension(wrapper.getDouble(DbStarField.RightAscension));
		result.setDeclination(wrapper.getDouble(DbStarField.Declination));
		result.setDistance(wrapper.getDouble(DbStarField.Distance));
		result.setProperMotionRightAscension(wrapper.getDouble(DbStarField.ProperMotionRightAscension));
		result.setProperMotionDeclination(wrapper.getDouble(DbStarField.ProperMotionDeclination));
		result.setRadialVelocity(wrapper.getDouble(DbStarField.RadialVelocity));
		result.setMagnitude(wrapper.getDouble(DbStarField.Magnitude));
		result.setAbsoluteMagnitude(wrapper.getDouble(DbStarField.AbsoluteMagnitude));
		result.setSpectrum(wrapper.getString(DbStarField.Spectrum));
		result.setColorIndex(wrapper.getDouble(DbStarField.ColorIndex));
		result.setX(wrapper.getDouble(DbStarField.X));
		result.setY(wrapper.getDouble(DbStarField.Y));
		result.setZ(wrapper.getDouble(DbStarField.Z));
		result.setVX(wrapper.getDouble(DbStarField.VX));
		result.setVY(wrapper.getDouble(DbStarField.VY));
		result.setVZ(wrapper.getDouble(DbStarField.VZ));
		result.setRightAcensionRadians(wrapper.getDouble(DbStarField.RightAcensionRadians));
		result.setDeclinationRadians(wrapper.getDouble(DbStarField.DeclinationRadians));
		result.setProperMotionRightAscensionRadians(wrapper.getDouble(DbStarField.ProperMotionRightAscensionRadians));
		result.setProperMotionDeclinationRadians(wrapper.getDouble(DbStarField.ProperMotionDeclinationRadians));
		result.setBayerId(wrapper.getString(DbStarField.BayerId));
		result.setFlamsteed(wrapper.getString(DbStarField.Flamsteed));
		result.setConstellation(wrapper.getString(DbStarField.Constellation));
		result.setCompanionStarId(wrapper.getString(DbStarField.CompanionStarId));
		result.setPrimaryStarId(wrapper.getString(DbStarField.PrimaryStarId));
		result.setMultipleStarId(wrapper.getString(DbStarField.MultipleStarId));
		result.setLuminosity(wrapper.getDouble(DbStarField.Luminosity));
		result.setVariableStarDesignation(wrapper.getString(DbStarField.VariableStarDesignation));
		result.setVariableMinimum(wrapper.getDouble(DbStarField.VariableMinimum));
		result.setVariableMaximum(wrapper.getDouble(DbStarField.VariableMaximum));

		return result;
	}

	public List<Star> createStars(Iterator<BaseEntity<Key>> entities) {
		List<Star> result = new ArrayList<Star>();

		int count = 0;
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createStar(entity));

			if ((++count) % 1000 == 0) {
				logger.info("createStars count = " + count);
			}
		}

		return result;
	}

	public Spaceship createSpaceship(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		Spaceship result = new Spaceship();
		result.setKey(DbEntity.Spaceship.createRestKey(entity.getKey()));
		result.setName(wrapper.getString(DbSpaceshipField.Name));
		result.setX(wrapper.getLong(DbSpaceshipField.X));
		result.setY(wrapper.getLong(DbSpaceshipField.Y));
		result.setX(wrapper.getLong(DbSpaceshipField.Z));
		result.setStarKey(DbEntity.Star.createRestKey(wrapper.getKey(DbSpaceshipField.StarKey)));

		return result;
	}

	public List<Spaceship> createSpaceships(Iterator<BaseEntity<Key>> entities) {
		List<Spaceship> result = new ArrayList<Spaceship>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createSpaceship(entity));
		}
		return result;
	}

	public CrewMember createCrewMember(BaseEntity<Key> entity) {

		DbEntityWrapper wrapper = DbEntityWrapper.wrap(entity);

		CrewMember result = new CrewMember();
		result.setKey(DbEntity.CrewMember.createRestKey(entity.getKey()));
		result.setFirstName(wrapper.getString(DbCrewMemberField.FirstName));
		result.setLastName(wrapper.getString(DbCrewMemberField.LastName));
		return result;
	}

	public List<CrewMember> createCrewMembers(Iterator<BaseEntity<Key>> entities) {
		List<CrewMember> result = new ArrayList<CrewMember>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createCrewMember(entity));
		}
		return result;
	}
}
