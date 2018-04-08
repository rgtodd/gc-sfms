package sfms.rest.db.business;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.FullEntity;

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
