package sfms.simulator.worker.functions;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;

import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorIterator;
import sfms.simulator.worker.Worker;
import sfms.simulator.worker.WorkerFunction;

public class UpdateActors implements WorkerFunction {

	private Datastore m_datastore;
	private Worker m_transactionWorker;
	private Instant m_now;

	public UpdateActors(Datastore datastore, Worker transactionWorker, Instant now) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (transactionWorker == null) {
			throw new IllegalArgumentException("Argument transactionWorker is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}

		m_datastore = datastore;
		m_transactionWorker = transactionWorker;
		m_now = now;
	}

	@Override
	public void execute() throws Exception {
		ActorDatasource datasource = new ActorDatasource(m_datastore);
		try (ActorIterator actors = datasource.getActors()) {
			while (actors.hasNext()) {
				m_transactionWorker
						.process(new UpdateActor(m_datastore, actors.next().getKey(), m_now));
			}
		}
	}

	@Override
	public String toString() {
		return "UpdateActors";
	}
}
