package sfms.simulator.worker.functions;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

import sfms.simulator.Actor;
import sfms.simulator.ActorDatasource;
import sfms.simulator.worker.WorkerFunction;

public class InitializeActor implements WorkerFunction {

	private Datastore m_datastore;
	private Key m_actorKey;
	private Instant m_now;
	private boolean m_reset;

	public InitializeActor(Datastore datastore, Key actorKey, Instant now, boolean reset) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (actorKey == null) {
			throw new IllegalArgumentException("Argument actorKey is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}

		m_datastore = datastore;
		m_actorKey = actorKey;
		m_now = now;
		m_reset = reset;
	}

	@Override
	public void execute() throws Exception {
		ActorDatasource datasource = new ActorDatasource(m_datastore);
		Actor actor = datasource.getActor(m_actorKey);
		actor.initialize(m_now, m_reset);
	}

	@Override
	public String toString() {
		return "InitializeActor[" + m_actorKey.toString() + "]";
	}

}
