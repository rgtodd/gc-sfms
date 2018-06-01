package sfms.simulator.worker.functions;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

import sfms.simulator.Actor;
import sfms.simulator.ActorDatasource;
import sfms.simulator.worker.WorkerFunction;

public class UpdateActor implements WorkerFunction {

	private Datastore m_datastore;
	private Key m_actorKey;
	private Instant m_now;

	public UpdateActor(Datastore datastore, Key actorKey, Instant now) {
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
	}

	@Override
	public void execute() {
		ActorDatasource datasource = new ActorDatasource(m_datastore);
		Actor actor = datasource.getActor(m_actorKey);
		actor.updateState(m_now);
	}

	@Override
	public String toString() {
		return "UpdateActor[" + m_actorKey.toString() + "]";
	}

}
