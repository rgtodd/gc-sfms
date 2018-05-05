package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.schemas.DbClusterField;
import sfms.rest.db.schemas.DbCrewMemberField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSectorField;
import sfms.rest.db.schemas.DbSpaceshipField;
import sfms.rest.db.schemas.DbStarField;

/**
 * Factory used to create REST entities from data store entities.
 * 
 */
public class RestFactory {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(RestFactory.class.getName());

	public Cluster createCluster(BaseEntity<Key> entity, List<Star> stars) {

		Cluster result = new Cluster();
		result.setKey(DbEntity.Cluster.createRestKey(entity.getKey()));
		result.setClusterPartition(getLong(entity, DbClusterField.ClusterPartition));
		result.setClusterX(getLong(entity, DbClusterField.ClusterX));
		result.setClusterY(getLong(entity, DbClusterField.ClusterY));
		result.setClusterZ(getLong(entity, DbClusterField.ClusterZ));
		result.setMinimumX(getLong(entity, DbClusterField.MinimumX));
		result.setMinimumY(getLong(entity, DbClusterField.MinimumY));
		result.setMinimumZ(getLong(entity, DbClusterField.MinimumZ));
		result.setMaximumX(getLong(entity, DbClusterField.MaximumX));
		result.setMaximumY(getLong(entity, DbClusterField.MaximumY));
		result.setMaximumZ(getLong(entity, DbClusterField.MaximumZ));
		result.setStars(stars);
		return result;
	}

	public List<Cluster> createClusters(Iterator<BaseEntity<Key>> entities) {
		List<Cluster> result = new ArrayList<Cluster>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createCluster(entity, null));
		}
		return result;
	}

	public List<Cluster> createClusters(QueryResults<Entity> entities) {
		return createClusters(new EntityIterator(entities));
	}

	public Sector createSector(BaseEntity<Key> entity, List<Star> stars) {

		Sector result = new Sector();
		result.setKey(DbEntity.Sector.createRestKey(entity.getKey()));
		result.setSectorX(getLong(entity, DbSectorField.SectorX));
		result.setSectorY(getLong(entity, DbSectorField.SectorY));
		result.setSectorZ(getLong(entity, DbSectorField.SectorZ));
		result.setMinimumX(getLong(entity, DbSectorField.MinimumX));
		result.setMinimumY(getLong(entity, DbSectorField.MinimumY));
		result.setMinimumZ(getLong(entity, DbSectorField.MinimumZ));
		result.setMaximumX(getLong(entity, DbSectorField.MaximumX));
		result.setMaximumY(getLong(entity, DbSectorField.MaximumY));
		result.setMaximumZ(getLong(entity, DbSectorField.MaximumZ));
		result.setStars(stars);
		return result;
	}

	public List<Sector> createSectors(Iterator<BaseEntity<Key>> entities) {
		List<Sector> result = new ArrayList<Sector>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createSector(entity, null));
		}
		return result;
	}

	public List<Sector> createSectors(QueryResults<Entity> entities) {
		return createSectors(new EntityIterator(entities));
	}

	public Star createStar(BaseEntity<Key> entity) {

		Star result = new Star();
		result.setKey(DbEntity.Star.createRestKey(entity.getKey()));
		result.setCatalogId(getString(entity, DbStarField.CatalogId));
		result.setClusterKey(DbEntity.Cluster.createRestKey(getKey(entity, DbStarField.ClusterKey)));
		result.setSectorKey(DbEntity.Sector.createRestKey(getKey(entity, DbStarField.SectorKey)));
		result.setHipparcosId(getString(entity, DbStarField.HipparcosId));
		result.setHenryDraperId(getString(entity, DbStarField.HenryDraperId));
		result.setHarvardRevisedId(getString(entity, DbStarField.HarvardRevisedId));
		result.setGlieseId(getString(entity, DbStarField.GlieseId));
		result.setBayerFlamsteedId(getString(entity, DbStarField.BayerFlamsteedId));
		result.setProperName(getString(entity, DbStarField.ProperName));
		result.setRightAscension(getDouble(entity, DbStarField.RightAscension));
		result.setDeclination(getDouble(entity, DbStarField.Declination));
		result.setDistance(getDouble(entity, DbStarField.Distance));
		result.setProperMotionRightAscension(getDouble(entity, DbStarField.ProperMotionRightAscension));
		result.setProperMotionDeclination(getDouble(entity, DbStarField.ProperMotionDeclination));
		result.setRadialVelocity(getDouble(entity, DbStarField.RadialVelocity));
		result.setMagnitude(getDouble(entity, DbStarField.Magnitude));
		result.setAbsoluteMagnitude(getDouble(entity, DbStarField.AbsoluteMagnitude));
		result.setSpectrum(getString(entity, DbStarField.Spectrum));
		result.setColorIndex(getDouble(entity, DbStarField.ColorIndex));
		result.setX(getDouble(entity, DbStarField.X));
		result.setY(getDouble(entity, DbStarField.Y));
		result.setZ(getDouble(entity, DbStarField.Z));
		result.setVX(getDouble(entity, DbStarField.VX));
		result.setVY(getDouble(entity, DbStarField.VY));
		result.setVZ(getDouble(entity, DbStarField.VZ));
		result.setRightAcensionRadians(getDouble(entity, DbStarField.RightAcensionRadians));
		result.setDeclinationRadians(getDouble(entity, DbStarField.DeclinationRadians));
		result.setProperMotionRightAscensionRadians(getDouble(entity, DbStarField.ProperMotionRightAscensionRadians));
		result.setProperMotionDeclinationRadians(getDouble(entity, DbStarField.ProperMotionDeclinationRadians));
		result.setBayerId(getString(entity, DbStarField.BayerId));
		result.setFlamsteed(getString(entity, DbStarField.Flamsteed));
		result.setConstellation(getString(entity, DbStarField.Constellation));
		result.setCompanionStarId(getString(entity, DbStarField.CompanionStarId));
		result.setPrimaryStarId(getString(entity, DbStarField.PrimaryStarId));
		result.setMultipleStarId(getString(entity, DbStarField.MultipleStarId));
		result.setLuminosity(getDouble(entity, DbStarField.Luminosity));
		result.setVariableStarDesignation(getString(entity, DbStarField.VariableStarDesignation));
		result.setVariableMinimum(getDouble(entity, DbStarField.VariableMinimum));
		result.setVariableMaximum(getDouble(entity, DbStarField.VariableMaximum));

		return result;
	}

	public List<Star> createStars(Iterator<BaseEntity<Key>> entities) {
		List<Star> result = new ArrayList<Star>();
		while (entities.hasNext()) {
			BaseEntity<Key> entity = entities.next();
			result.add(createStar(entity));
		}
		return result;
	}

	public List<Star> createStars(QueryResults<Entity> entities) {
		return createStars(new EntityIterator(entities));
	}

	public List<Star> createStarsFromProjection(QueryResults<ProjectionEntity> entities) {
		return createStars(new ProjectionEntityIterator(entities));
	}

	public Spaceship createSpaceship(BaseEntity<Key> entity) {
		Spaceship result = new Spaceship();
		result.setKey(DbEntity.Spaceship.createRestKey(entity.getKey()));
		result.setName(getString(entity, DbSpaceshipField.Name));
		result.setX(getLong(entity, DbSpaceshipField.X));
		result.setY(getLong(entity, DbSpaceshipField.Y));
		result.setX(getLong(entity, DbSpaceshipField.Z));
		result.setStarKey(DbEntity.Star.createRestKey(getKey(entity, DbSpaceshipField.StarKey)));

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

	public List<Spaceship> createSpaceships(QueryResults<Entity> entities) {
		return createSpaceships(new EntityIterator(entities));
	}

	public CrewMember createCrewMember(BaseEntity<Key> entity) {
		CrewMember result = new CrewMember();
		result.setKey(DbEntity.CrewMember.createRestKey(entity.getKey()));
		result.setFirstName(getString(entity, DbCrewMemberField.FirstName));
		result.setLastName(getString(entity, DbCrewMemberField.LastName));
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

	public List<CrewMember> createCrewMembers(QueryResults<Entity> entities) {
		return createCrewMembers(new EntityIterator(entities));
	}

	private Key getKey(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getName();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getKey(name);
	}

	private String getString(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getName();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getString(name);
	}

	private Double getDouble(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getName();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getDouble(name);
	}

	private long getLong(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getName();
		if (!entity.contains(name)) {
			return 0;
		}
		if (entity.isNull(name)) {
			return 0;
		}
		return entity.getLong(name);
	}

	private static class EntityIterator implements Iterator<BaseEntity<Key>> {

		private QueryResults<Entity> m_results;

		public EntityIterator(QueryResults<Entity> results) {
			m_results = results;
		}

		@Override
		public boolean hasNext() {
			return m_results.hasNext();
		}

		@Override
		public BaseEntity<Key> next() {
			return m_results.next();
		}
	}

	private static class ProjectionEntityIterator implements Iterator<BaseEntity<Key>> {

		private QueryResults<ProjectionEntity> m_results;

		public ProjectionEntityIterator(QueryResults<ProjectionEntity> results) {
			m_results = results;
		}

		@Override
		public boolean hasNext() {
			return m_results.hasNext();
		}

		@Override
		public BaseEntity<Key> next() {
			return m_results.next();
		}

	}
}
