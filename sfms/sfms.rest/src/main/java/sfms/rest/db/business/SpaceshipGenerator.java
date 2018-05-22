package sfms.rest.db.business;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.KeyFactory;

import sfms.db.schemas.DbEntity;
import sfms.db.schemas.DbSpaceshipField;
import sfms.rest.api.test.ValueGenerator;

/**
 * Factory class used to generate test Spaceship entities.
 *
 */
public class SpaceshipGenerator {

	private final Logger logger = Logger.getLogger(SpaceshipGenerator.class.getName());

	public void generate(int count) {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		KeyFactory clusterKeyFactory = datastore.newKeyFactory().setKind(DbEntity.Spaceship.getKind());

		try (BatchPut batchPut = new BatchPut(datastore)) {
			for (int idx = 0; idx < count; ++idx) {
				String name = "USS " + ValueGenerator.getRandomAdjective() + " " + ValueGenerator.getRandomNoun();

				IncompleteKey clusterKey = clusterKeyFactory.newKey();
				FullEntity<IncompleteKey> cluster = FullEntity.newBuilder(clusterKey)
						.set(DbSpaceshipField.Name.getName(), name).build();
				batchPut.add(cluster);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "generate exception occurred.", e);
		}
	}
}
