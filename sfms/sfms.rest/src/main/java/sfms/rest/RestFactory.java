package sfms.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.cloud.datastore.Entity;

import sfms.rest.api.models.CrewMember;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.models.Star;
import sfms.rest.db.schemas.DbCrewMemberField;
import sfms.rest.db.schemas.DbSpaceshipField;
import sfms.rest.db.schemas.DbStarField;

public class RestFactory {

	public Star createStar(Entity entity) {
		Star result = new Star();
		result.setKey(entity.getKey().getId().toString());
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

	public List<Star> createStars(Iterator<Entity> entities) {
		List<Star> result = new ArrayList<Star>();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			result.add(createStar(entity));
		}
		return result;
	}

	public Spaceship createSpaceship(Entity entity) {
		Spaceship result = new Spaceship();
		result.setKey(entity.getKey().getId().toString());
		result.setName(entity.getString(DbSpaceshipField.Name.getId()));
		return result;
	}

	public List<Spaceship> createSpaceships(Iterator<Entity> entities) {
		List<Spaceship> result = new ArrayList<Spaceship>();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			result.add(createSpaceship(entity));
		}
		return result;
	}

	public CrewMember createCrewMember(Entity entity) {
		CrewMember result = new CrewMember();
		result.setKey(entity.getKey().getId().toString());
		result.setFirstName(entity.getString(DbCrewMemberField.FirstName.getId()));
		result.setLastName(entity.getString(DbCrewMemberField.LastName.getId()));
		return result;
	}

	public List<CrewMember> createCrewMembers(Iterator<Entity> entities) {
		List<CrewMember> result = new ArrayList<CrewMember>();
		while (entities.hasNext()) {
			Entity entity = entities.next();
			result.add(createCrewMember(entity));
		}
		return result;
	}

	private String getString(Entity entity, DbStarField field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getString(name);
	}

	private Double getOptionalDouble(Entity entity, DbStarField field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return null;
		}
		if (entity.isNull(name)) {
			return null;
		}
		return entity.getDouble(name);
	}

	private double getDouble(Entity entity, DbStarField field) {
		String name = field.getId();
		if (!entity.contains(name)) {
			return 0;
		}
		if (entity.isNull(name)) {
			return 0;
		}
		return entity.getDouble(name);
	}

}
