package sfms.simulator.worker.functions;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;

import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorIterator;
import sfms.simulator.MissionGenerator;
import sfms.simulator.worker.Worker;
import sfms.simulator.worker.WorkerFunction;

public class CreateMissions implements WorkerFunction {

	private Datastore m_datastore;
	private Worker m_transactionWorker;
	private Instant m_now;
	private MissionGenerator m_missionGenerator;
	private boolean m_reset;

	public CreateMissions(Datastore datastore, Worker transactionWorker, Instant now, MissionGenerator missionGenerator,
			boolean reset) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (transactionWorker == null) {
			throw new IllegalArgumentException("Argument transactionWorker is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}
		if (missionGenerator == null) {
			throw new IllegalArgumentException("Argument missionGenerator is null.");
		}

		m_datastore = datastore;
		m_transactionWorker = transactionWorker;
		m_now = now;
		m_missionGenerator = missionGenerator;
		m_reset = reset;
	}

	@Override
	public void execute() throws Exception {
		ActorDatasource datasource = new ActorDatasource(m_datastore);
		try (ActorIterator actors = datasource.getActors()) {
			while (actors.hasNext()) {
				m_transactionWorker
						.process(new CreateMission(m_datastore, actors.next().getActorKey(), m_now, m_missionGenerator,
								m_reset));
			}
		}
	}

	@Override
	public String toString() {
		return "CreateMissions";
	}
}
