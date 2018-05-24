package sfms.db;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.FullEntity;

/**
 * Manages batches of operations to be sent to the data store.
 * 
 * Operations are added to the batch using the {@link #add} method. When the
 * batch is full, the operations are sent to the data store with a single PUT
 * operation.
 * 
 * When the batch is closed, any outstanding operations are sent to the data
 * store.
 *
 */
public class BatchPut implements AutoCloseable {

	private static final int BATCH_SIZE = 100;
	private static final FullEntity<?>[] FULL_ENTITY_ARRAY_PROTOTYPE = new FullEntity<?>[0];

	private Datastore m_datastore;
	private List<FullEntity<?>> m_entities = new ArrayList<FullEntity<?>>();

	public BatchPut(Datastore datastore) {
		m_datastore = datastore;
	}

	public void add(FullEntity<?> entity) {
		m_entities.add(entity);
		if (m_entities.size() >= BATCH_SIZE) {
			flush();
		}
	}

	public void flush() {
		if (m_entities.size() > 0) {
			m_datastore.put(m_entities.toArray(FULL_ENTITY_ARRAY_PROTOTYPE));
			m_entities.clear();
		}
	}

	@Override
	public void close() throws Exception {
		flush();
	}
}
