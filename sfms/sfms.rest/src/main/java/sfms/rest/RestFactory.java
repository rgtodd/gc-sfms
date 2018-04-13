package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.QueryResults;

import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.rest.db.DbFieldSchema;
import sfms.rest.db.schemas.DbClusterField;
import sfms.rest.db.schemas.DbCrewMemberField;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbSpaceshipField;
import sfms.rest.db.schemas.DbStarField;

public class RestFactory {

	private final Logger logger = Logger.getLogger(RestFactory.class.getName());

	public Cluster createCluster(BaseEntity<Key> entity, List<Star> stars) {

		Cluster result = new Cluster();
		result.setKey(DbEntity.Cluster.getRestKeyProvider().apply(entity.getKey()));
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

	public Star createStar(BaseEntity<Key> entity) {

		logger.log(Level.INFO, "Creating star {0}", entity.getKey());

		Star result = new Star();
		result.setKey(DbEntity.Star.getRestKeyProvider().apply(entity.getKey()));
		result.setClusterKey(
				DbEntity.Cluster.getRestKeyProvider().apply(getKey(entity, DbStarField.ClusterKey)));
		result.setSectorKey(
				DbEntity.Sector.getRestKeyProvider().apply(getKey(entity, DbStarField.SectorKey)));
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
		result.setColorIndex(getOptionalDouble(entity, DbStarField.ColorIndex));
		result.setX(getDouble(entity, DbStarField.X));
		result.setY(getDouble(entity, DbStarField.Y));
		result.setZ(getDouble(entity, DbStarField.Z));
		result.setVx(getDouble(entity, DbStarField.VX));
		result.setVy(getDouble(entity, DbStarField.VY));
		result.setVz(getDouble(entity, DbStarField.VZ));
		result.setRightAcensionRadians(getDouble(entity, DbStarField.RightAcensionRadians));
		result.setDeclinationRadians(getDouble(entity, DbStarField.DeclinationRadians));
		result.setProperMotionRightAscensionRadians(
				getDouble(entity, DbStarField.ProperMotionRightAscensionRadians));
		result.setProperMotionDeclinationRadians(getDouble(entity, DbStarField.ProperMotionDeclinationRadians));
		result.setBayerId(getString(entity, DbStarField.BayerId));
		result.setFlamsteed(getString(entity, DbStarField.Flamsteed));
		result.setConstellation(getString(entity, DbStarField.Constellation));
		result.setCompanionStarId(getString(entity, DbStarField.CompanionStarId));
		result.setPrimaryStarId(getString(entity, DbStarField.PrimaryStarId));
		result.setMultipleStarId(getString(entity, DbStarField.MultipleStarId));
		result.setLuminosity(getDouble(entity, DbStarField.Luminosity));
		result.setVariableStarDesignation(getString(entity, DbStarField.VariableStarDesignation));
		result.setVariableMinimum(getOptionalDouble(entity, DbStarField.VariableMinimum));
		result.setVariableMaximum(getOptionalDouble(entity, DbStarField.VariableMaximum));

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
		result.setKey(DbEntity.Spaceship.getRestKeyProvider().apply(entity.getKey()));
		result.setName(entity.getString(DbSpaceshipField.Name.getId()));
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
		result.setKey(DbEntity.CrewMember.getRestKeyProvider().apply(entity.getKey()));
		result.setFirstName(entity.getString(DbCrewMemberField.FirstName.getId()));
		result.setLastName(entity.getString(DbCrewMemberField.LastName.getId()));
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
		String name = field.getId();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getKey(name);
	}

	private String getString(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getString(name);
	}

	private Double getOptionalDouble(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getDouble(name);
	}

	private double getDouble(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return 0;
		}
		if (entity.isNull(name)) {
			return 0;
		}
		return entity.getDouble(name);
	}

	@SuppressWarnings("unused")
	private Long getOptionalLong(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getLong(name);
	}

	private long getLong(BaseEntity<Key> entity, DbFieldSchema field) {
		String name = field.getId();
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
