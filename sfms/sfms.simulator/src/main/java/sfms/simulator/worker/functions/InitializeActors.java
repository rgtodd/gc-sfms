package sfms.simulator.worker.functions;

import java.time.Instant;

import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorIterator;
import sfms.simulator.worker.Worker;
import sfms.simulator.worker.WorkerFunction;

public class InitializeActors implements WorkerFunction {

	private Worker m_transactionWorker;
	private Instant m_now;

	public InitializeActors(Worker transactionWorker, Instant now) {
		if (transactionWorker == null) {
			throw new IllegalArgumentException("Argument transactionWorker is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}

		m_transactionWorker = transactionWorker;
		m_now = now;
	}

	@Override
	public void execute() throws Exception {
		ActorDatasource datasource = new ActorDatasource();
		try (ActorIterator actors = datasource.getActors()) {
			while (actors.hasNext()) {
				m_transactionWorker.process(new InitializeActor(actors.next().getKey(), m_now));
			}
		}
	}

	@Override
	public String toString() {
		return "InitializeActors";
	}
}