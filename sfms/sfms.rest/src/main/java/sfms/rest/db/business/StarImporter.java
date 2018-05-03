package sfms.rest.db.business;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

import sfms.rest.db.CsvValue;
import sfms.rest.db.DbValueFactory;
import sfms.rest.db.schemas.DbEntity;
import sfms.rest.db.schemas.DbStarField;
import sfms.storage.Storage;

/**
 * Processes a CSV field containing star data and creates the associated Star
 * entities.
 * 
 */
public class StarImporter {

	private final Logger logger = Logger.getLogger(StarImporter.class.getName());

	private static final int LOG_INTERVAL = 500;
	// private static final int PROD_RECORD_LIMIT = 250000;
	// private static final int DEV_RECORD_LIMIT = 25000;

	private static final String FIELD_DELIMITER_REXEX = ",";

	private static final int FIELD_CatalogId = 0;
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

	private Datastore m_datastore;
	private KeyFactory m_clusterKeyFactory;
	private KeyFactory m_sectorKeyFactory;
	private RegionSet m_clusters;
	private RegionSet m_sectors;

	public void initialize() {
		m_datastore = DatastoreOptions.getDefaultInstance().getService();
		m_clusterKeyFactory = m_datastore.newKeyFactory().setKind(DbEntity.Cluster.getKind());
		m_sectorKeyFactory = m_datastore.newKeyFactory().setKind(DbEntity.Sector.getKind());
		m_clusters = RegionSet.loadClusters();
		m_sectors = RegionSet.loadSectors();
	}

	public void process(String bucketName, String blobName, int startIndex, int recordLimit) throws Exception {

		logger.log(Level.INFO, "Processing {0} / {1} / {2} / {3}.",
				new Object[] { bucketName, blobName, startIndex, recordLimit });

		try (ReadableByteChannel readChannel = Storage.getManager().getReadableByteChannel(bucketName, blobName);
				InputStream inputStream = Channels.newInputStream(readChannel);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				Stream<String> lineStream = bufferedReader.lines()) {

			processStarFileData(lineStream, startIndex, recordLimit);
		}
	}

	private void processStarFileData(Stream<String> lineStream, int startIndex, int recordLimit) {

		Iterator<String> iterator = lineStream.iterator();

		// Read in file header.
		//
		if (!iterator.hasNext()) {
			return;
		}
		@SuppressWarnings("unused")
		String heading = iterator.next();

		// Skip over records until we reach startIndex.
		//
		for (int idx = 0; idx < startIndex; ++idx) {
			if (!iterator.hasNext()) {
				return;
			}
			iterator.next();
		}

		//
		// int recordLimit;
		// if (PropertyFile.INSTANCE.isProduction()) {
		// recordLimit = PROD_RECORD_LIMIT;
		// } else {
		// recordLimit = DEV_RECORD_LIMIT;
		// }

		try (BatchPut batchPut = new BatchPut(m_datastore)) {
			int count = 0;
			while (iterator.hasNext() && count < recordLimit) {
				count += 1;
				if (count % LOG_INTERVAL == 0) {
					logger.log(Level.INFO, "Processing record # {0}.", count);
				}

				String line = iterator.next();
				processStarFileDataLine(batchPut, line);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error during import.", e);
		}
	}

	private void processStarFileDataLine(BatchPut batchPut, String line) throws Exception {

		String[] fields = line.split(FIELD_DELIMITER_REXEX);

		Long catalogId = CsvValue.getLong(fields, FIELD_CatalogId);
		String hipparcosId = CsvValue.getString(fields, FIELD_HipparcosId, "0");
		String henryDraperId = CsvValue.getString(fields, FIELD_HenryDraperId);
		String harvardRevisedId = CsvValue.getString(fields, FIELD_HarvardRevisedId);
		String glieseId = CsvValue.getString(fields, FIELD_GlieseId);
		String bayerFlamsteedId = CsvValue.getString(fields, FIELD_BayerFlamsteedId);
		String properName = CsvValue.getString(fields, FIELD_ProperName);
		double rightAscension = CsvValue.getDouble(fields, FIELD_RightAscension);
		double declination = CsvValue.getDouble(fields, FIELD_Declination);
		double distance = CsvValue.getDouble(fields, FIELD_Distance);
		double properMotionRightAscension = CsvValue.getDouble(fields, FIELD_ProperMotionRightAscension);
		double properMotionDeclination = CsvValue.getDouble(fields, FIELD_ProperMotionDeclination);
		double radialVelocity = CsvValue.getDouble(fields, FIELD_RadialVelocity);
		double magnitude = CsvValue.getDouble(fields, FIELD_Magnitude);
		double absoluteMagnitude = CsvValue.getDouble(fields, FIELD_AbsoluteMagnitude);
		String spectrum = CsvValue.getString(fields, FIELD_Spectrum);
		Double colorIndex = CsvValue.getOptionalDouble(fields, FIELD_ColorIndex);
		double x = CsvValue.getDouble(fields, FIELD_X);
		double y = CsvValue.getDouble(fields, FIELD_Y);
		double z = CsvValue.getDouble(fields, FIELD_Z);
		double vx = CsvValue.getDouble(fields, FIELD_VX);
		double vy = CsvValue.getDouble(fields, FIELD_VY);
		double vz = CsvValue.getDouble(fields, FIELD_VZ);
		double rightAcensionRadians = CsvValue.getDouble(fields, FIELD_RightAcensionRadians);
		double declinationRadians = CsvValue.getDouble(fields, FIELD_DeclinationRadians);
		double properMotionRightAscensionRadians = CsvValue.getDouble(fields, FIELD_ProperMotionRightAscensionRadians);
		double properMotionDeclinationRadians = CsvValue.getDouble(fields, FIELD_ProperMotionDeclinationRadians);
		String bayerId = CsvValue.getString(fields, FIELD_BayerId);
		String flamsteed = CsvValue.getString(fields, FIELD_Flamsteed);
		String constellation = CsvValue.getString(fields, FIELD_Constellation);
		String companionStarId = CsvValue.getString(fields, FIELD_CompanionStarId);
		String primaryStarId = CsvValue.getString(fields, FIELD_PrimaryStarId);
		String multipleStarId = CsvValue.getString(fields, FIELD_MultipleStarId);
		double luminosity = CsvValue.getDouble(fields, FIELD_Luminosity);
		String variableStarDesignation = CsvValue.getString(fields, FIELD_VariableStarDesignation);
		Double variableMinimum = CsvValue.getOptionalDouble(fields, FIELD_VariableMinimum);
		Double variableMaximum = CsvValue.getOptionalDouble(fields, FIELD_VariableMaximum);

		int catalogIdHash = generateHash(catalogId.hashCode());
		String keyValue = String.valueOf(catalogIdHash) + "-" + String.valueOf(catalogId);

		Region cluster = m_clusters.findClosestRegion(x, y, z);
		Key clusterKey = cluster != null ? m_clusterKeyFactory.newKey(cluster.getKey()) : null;

		Region sector = m_sectors.findContainingRegion(x, y, z);
		Key sectorKey = sector != null ? m_sectorKeyFactory.newKey(sector.getKey()) : null;

		Key key = DbEntity.Star.createEntityKey(m_datastore, keyValue);

		Entity entity = Entity.newBuilder(key)
				// Indexed columns
				.set(DbStarField.CatalogId.getName(), DbValueFactory.asValue(catalogId.toString()))
				.set(DbStarField.ClusterKey.getName(), DbValueFactory.asValue(clusterKey))
				.set(DbStarField.SectorKey.getName(), DbValueFactory.asValue(sectorKey))
				.set(DbStarField.X.getName(), DbValueFactory.asValue(x))
				.set(DbStarField.Y.getName(), DbValueFactory.asValue(y))
				.set(DbStarField.Z.getName(), DbValueFactory.asValue(z))
				.set(DbStarField.HipparcosId.getName(), DbValueFactory.asValue(hipparcosId))
				// Unindexed columns
				.set(DbStarField.HenryDraperId.getName(), DbValueFactory.asUnindexedValue(henryDraperId))
				.set(DbStarField.HarvardRevisedId.getName(), DbValueFactory.asUnindexedValue(harvardRevisedId))
				.set(DbStarField.GlieseId.getName(), DbValueFactory.asUnindexedValue(glieseId))
				.set(DbStarField.BayerFlamsteedId.getName(), DbValueFactory.asUnindexedValue(bayerFlamsteedId))
				.set(DbStarField.ProperName.getName(), DbValueFactory.asUnindexedValue(properName))
				.set(DbStarField.RightAscension.getName(), DbValueFactory.asUnindexedValue(rightAscension))
				.set(DbStarField.Declination.getName(), DbValueFactory.asUnindexedValue(declination))
				.set(DbStarField.Distance.getName(), DbValueFactory.asUnindexedValue(distance))
				.set(DbStarField.ProperMotionRightAscension.getName(),
						DbValueFactory.asUnindexedValue(properMotionRightAscension))
				.set(DbStarField.ProperMotionDeclination.getName(),
						DbValueFactory.asUnindexedValue(properMotionDeclination))
				.set(DbStarField.RadialVelocity.getName(), DbValueFactory.asUnindexedValue(radialVelocity))
				.set(DbStarField.Magnitude.getName(), DbValueFactory.asUnindexedValue(magnitude))
				.set(DbStarField.AbsoluteMagnitude.getName(), DbValueFactory.asUnindexedValue(absoluteMagnitude))
				.set(DbStarField.Spectrum.getName(), DbValueFactory.asUnindexedValue(spectrum))
				.set(DbStarField.ColorIndex.getName(), DbValueFactory.asUnindexedValue(colorIndex))
				.set(DbStarField.VX.getName(), DbValueFactory.asUnindexedValue(vx))
				.set(DbStarField.VY.getName(), DbValueFactory.asUnindexedValue(vy))
				.set(DbStarField.VZ.getName(), DbValueFactory.asUnindexedValue(vz))
				.set(DbStarField.RightAcensionRadians.getName(), DbValueFactory.asUnindexedValue(rightAcensionRadians))
				.set(DbStarField.DeclinationRadians.getName(), DbValueFactory.asUnindexedValue(declinationRadians))
				.set(DbStarField.ProperMotionRightAscensionRadians.getName(),
						DbValueFactory.asUnindexedValue(properMotionRightAscensionRadians))
				.set(DbStarField.ProperMotionDeclinationRadians.getName(),
						DbValueFactory.asUnindexedValue(properMotionDeclinationRadians))
				.set(DbStarField.BayerId.getName(), DbValueFactory.asUnindexedValue(bayerId))
				.set(DbStarField.Flamsteed.getName(), DbValueFactory.asUnindexedValue(flamsteed))
				.set(DbStarField.Constellation.getName(), DbValueFactory.asUnindexedValue(constellation))
				.set(DbStarField.CompanionStarId.getName(), DbValueFactory.asUnindexedValue(companionStarId))
				.set(DbStarField.PrimaryStarId.getName(), DbValueFactory.asUnindexedValue(primaryStarId))
				.set(DbStarField.MultipleStarId.getName(), DbValueFactory.asUnindexedValue(multipleStarId))
				.set(DbStarField.Luminosity.getName(), DbValueFactory.asUnindexedValue(luminosity))
				.set(DbStarField.VariableStarDesignation.getName(),
						DbValueFactory.asUnindexedValue(variableStarDesignation))
				.set(DbStarField.VariableMinimum.getName(), DbValueFactory.asUnindexedValue(variableMinimum))
				.set(DbStarField.VariableMaximum.getName(), DbValueFactory.asUnindexedValue(variableMaximum)).build();

		batchPut.add(entity);
	}

	private int generateHash(int key) {
		key = ~key + (key << 15); // key = (key << 15) - key - 1;
		key = key ^ (key >>> 12);
		key = key + (key << 2);
		key = key ^ (key >>> 4);
		key = key * 2057; // key = (key + (key << 3)) + (key << 11);
		key = key ^ (key >>> 16);
		return Math.abs(key);
	}

}
