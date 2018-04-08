package sfms.rest.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.ReadChannel;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;

@RestController
@RequestMapping("/task")
public class TaskRestController {

	private static final String FIELD_DELIMITER_REXEX = ",";

	private static final int FIELD_StarId = 0;
	private static final int FIELD_HipparcosId = 1;
	private static final int FIELD_HenryDraperId = 2;
	private static final int FIELD_HarvardRevisedId = 3;
	private static final int FIELD_GlieseId = 4;
	private static final int FIELD_BayerFlamsteedId = 5;
	private static final int FIELD_ProperName = 6;
	private static final int FIELD_RightAscension = 7;
	private static final int FIELD_Declination = 8;
	private static final int FIELD_Distance = 9;
	private static final int FIELD_ProperMotionRightAscension = 10;
	private static final int FIELD_ProperMotionDeclination = 11;
	private static final int FIELD_RadialVelocity = 12;
	private static final int FIELD_Magnitude = 13;
	private static final int FIELD_AbsoluteMagnitude = 14;
	private static final int FIELD_Spectrum = 15;
	private static final int FIELD_ColorIndex = 16;
	private static final int FIELD_X = 17;
	private static final int FIELD_Y = 18;
	private static final int FIELD_Z = 19;
	private static final int FIELD_VX = 20;
	private static final int FIELD_VY = 21;
	private static final int FIELD_VZ = 22;
	private static final int FIELD_RightAcensionRadians = 23;
	private static final int FIELD_DeclinationRadians = 24;
	private static final int FIELD_ProperMotionRightAscensionRadians = 25;
	private static final int FIELD_ProperMotionDeclinationRadians = 26;
	private static final int FIELD_BayerId = 27;
	private static final int FIELD_Flamsteed = 28;
	private static final int FIELD_Constellation = 29;
	private static final int FIELD_CompanionStarId = 30;
	private static final int FIELD_PrimaryStarId = 31;
	private static final int FIELD_MultipleStarId = 32;
	private static final int FIELD_Luminosity = 33;
	private static final int FIELD_VariableStarDesignation = 34;
	private static final int FIELD_VariableMinimum = 35;
	private static final int FIELD_VariableMaximum = 36;

	private final Logger logger = Logger.getLogger(TaskRestController.class.getName());

	@GetMapping(value = "/processStarFile")
	public void processStarFile(@RequestParam("filename") String filename) throws Exception {

		logger.log(Level.INFO, "Processing {0}.", filename);

		String bucketName = "rgt-ssms.appspot.com";
		String blobName = "uploads/" + filename;
		BlobId blobId = BlobId.of(bucketName, blobName);

		Storage storage = StorageOptions.getDefaultInstance().getService();
		try (ReadChannel readChannel = storage.reader(blobId);
				InputStream inputStream = Channels.newInputStream(readChannel);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				Stream<String> lineStream = bufferedReader.lines()) {

			processStarFileData(lineStream);
		}
	}

	private void processStarFileData(Stream<String> lineStream) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Iterator<String> iterator = lineStream.iterator();

		// Read in file header.
		//
		if (!iterator.hasNext()) {
			return;
		}
		@SuppressWarnings("unused")
		String heading = iterator.next();

		int count = 0;
		while (iterator.hasNext() && ++count <= 10) {
			String line = iterator.next();
			processStarFileDataLine(datastore, line);
		}
	}

	private void processStarFileDataLine(Datastore datastore, String line) {

		logger.log(Level.INFO, "Processing line: {0}.", line);

		String[] fields = line.split(FIELD_DELIMITER_REXEX);

		long starId = getLong(fields, FIELD_StarId) + 1000000;
		String hipparcosId = getString(fields, FIELD_HipparcosId);
		String henryDraperId = getString(fields, FIELD_HenryDraperId);
		String harvardRevisedId = getString(fields, FIELD_HarvardRevisedId);
		String glieseId = getString(fields, FIELD_GlieseId);
		String bayerFlamsteedId = getString(fields, FIELD_BayerFlamsteedId);
		String properName = getString(fields, FIELD_ProperName);
		double rightAscension = getDouble(fields, FIELD_RightAscension);
		double declination = getDouble(fields, FIELD_Declination);
		double distance = getDouble(fields, FIELD_Distance);
		double properMotionRightAscension = getDouble(fields, FIELD_ProperMotionRightAscension);
		double properMotionDeclination = getDouble(fields, FIELD_ProperMotionDeclination);
		double radialVelocity = getDouble(fields, FIELD_RadialVelocity);
		double magnitude = getDouble(fields, FIELD_Magnitude);
		double absoluteMagnitude = getDouble(fields, FIELD_AbsoluteMagnitude);
		String spectrum = getString(fields, FIELD_Spectrum);
		Double colorIndex = getOptionalDouble(fields, FIELD_ColorIndex);
		double x = getDouble(fields, FIELD_X);
		double y = getDouble(fields, FIELD_Y);
		double z = getDouble(fields, FIELD_Z);
		double vx = getDouble(fields, FIELD_VX);
		double vy = getDouble(fields, FIELD_VY);
		double vz = getDouble(fields, FIELD_VZ);
		double rightAcensionRadians = getDouble(fields, FIELD_RightAcensionRadians);
		double declinationRadians = getDouble(fields, FIELD_DeclinationRadians);
		double properMotionRightAscensionRadians = getDouble(fields, FIELD_ProperMotionRightAscensionRadians);
		double properMotionDeclinationRadians = getDouble(fields, FIELD_ProperMotionDeclinationRadians);
		String bayerId = getString(fields, FIELD_BayerId);
		String flamsteed = getString(fields, FIELD_Flamsteed);
		String constellation = getString(fields, FIELD_Constellation);
		String companionStarId = getString(fields, FIELD_CompanionStarId);
		String primaryStarId = getString(fields, FIELD_PrimaryStarId);
		String multipleStarId = getString(fields, FIELD_MultipleStarId);
		double luminosity = getDouble(fields, FIELD_Luminosity);
		String variableStarDesignation = getString(fields, FIELD_VariableStarDesignation);
		Double variableMinimum = getOptionalDouble(fields, FIELD_VariableMinimum);
		Double variableMaximum = getOptionalDouble(fields, FIELD_VariableMaximum);

		Key key = datastore
				.newKeyFactory()
				.setKind(DbEntity.Star.getKind())
				.newKey(starId);

		Entity entity = Entity.newBuilder(key)
				.set(DbStarField.ClusterKey.getId(), NullValue.of())
				.set(DbStarField.SectorKey.getId(), NullValue.of())
				.set(DbStarField.HipparcosId.getId(), asValue(hipparcosId))
				.set(DbStarField.HenryDraperId.getId(), asValue(henryDraperId))
				.set(DbStarField.HarvardRevisedId.getId(), asValue(harvardRevisedId))
				.set(DbStarField.GlieseId.getId(), asValue(glieseId))
				.set(DbStarField.BayerFlamsteedId.getId(), asValue(bayerFlamsteedId))
				.set(DbStarField.ProperName.getId(), asValue(properName))
				.set(DbStarField.RightAscension.getId(), asValue(rightAscension))
				.set(DbStarField.Declination.getId(), asValue(declination))
				.set(DbStarField.Distance.getId(), asValue(distance))
				.set(DbStarField.ProperMotionRightAscension.getId(), asValue(properMotionRightAscension))
				.set(DbStarField.ProperMotionDeclination.getId(), asValue(properMotionDeclination))
				.set(DbStarField.RadialVelocity.getId(), asValue(radialVelocity))
				.set(DbStarField.Magnitude.getId(), asValue(magnitude))
				.set(DbStarField.AbsoluteMagnitude.getId(), asValue(absoluteMagnitude))
				.set(DbStarField.Spectrum.getId(), asValue(spectrum))
				.set(DbStarField.ColorIndex.getId(), asValue(colorIndex))
				.set(DbStarField.X.getId(), asValue(x))
				.set(DbStarField.Y.getId(), asValue(y))
				.set(DbStarField.Z.getId(), asValue(z))
				.set(DbStarField.VX.getId(), asValue(vx))
				.set(DbStarField.VY.getId(), asValue(vy))
				.set(DbStarField.VZ.getId(), asValue(vz))
				.set(DbStarField.RightAcensionRadians.getId(), asValue(rightAcensionRadians))
				.set(DbStarField.DeclinationRadians.getId(), asValue(declinationRadians))
				.set(DbStarField.ProperMotionRightAscensionRadians.getId(),
						asValue(properMotionRightAscensionRadians))
				.set(DbStarField.ProperMotionDeclinationRadians.getId(), asValue(properMotionDeclinationRadians))
				.set(DbStarField.BayerId.getId(), asValue(bayerId))
				.set(DbStarField.Flamsteed.getId(), asValue(flamsteed))
				.set(DbStarField.Constellation.getId(), asValue(constellation))
				.set(DbStarField.CompanionStarId.getId(), asValue(companionStarId))
				.set(DbStarField.PrimaryStarId.getId(), asValue(primaryStarId))
				.set(DbStarField.MultipleStarId.getId(), asValue(multipleStarId))
				.set(DbStarField.Luminosity.getId(), asValue(luminosity))
				.set(DbStarField.VariableStarDesignation.getId(), asValue(variableStarDesignation))
				.set(DbStarField.VariableMinimum.getId(), asValue(variableMinimum))
				.set(DbStarField.VariableMaximum.getId(), asValue(variableMaximum))
				.build();

		datastore.put(entity);
	}

	private Value<?> asValue(String value) {
		if (value == null)
			return NullValue.of();
		return StringValue.of(value);
	}

	private Value<?> asValue(Double value) {
		if (value == null)
			return NullValue.of();
		return DoubleValue.of(value);
	}

	private String getString(String[] values, int index) {
		return processString(getValue(values, index));
	}

	private double getDouble(String[] values, int index) {
		return processDouble(getValue(values, index));
	}

	private long getLong(String[] values, int index) {
		return processLong(getValue(values, index));
	}

	private Double getOptionalDouble(String[] values, int index) {
		return processOptionalDouble(getValue(values, index));
	}

	private String getValue(String[] values, int index) {
		if (index < values.length) {
			return values[index];
		}
		return null;
	}

	private String processString(String value) {
		return value;
	}

	private double processDouble(String value) {
		return Double.parseDouble(value);
	}

	private long processLong(String value) {
		return Long.parseLong(value);
	}

	private Double processOptionalDouble(String value) {
		if (value == null)
			return null;
		return Double.parseDouble(value);
	}
}
