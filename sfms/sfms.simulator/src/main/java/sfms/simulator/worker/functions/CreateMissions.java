package sfms.simulator.worker.functions;

import java.time.Instant;

import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorIterator;
import sfms.simulator.MissionGenerator;
import sfms.simulator.worker.Worker;
import sfms.simulator.worker.WorkerFunction;

public class CreateMissions implements WorkerFunction {

	private Worker m_transactionWorker;
	private Instant m_now;
	private MissionGenerator m_missionGenerator;
	private boolean m_reset;

	public CreateMissions(Worker transactionWorker, Instant now, MissionGenerator missionGenerator, boolean reset) {
		if (transactionWorker == null) {
			throw new IllegalArgumentException("Argument transactionWorker is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}
		if (missionGenerator == null) {
			throw new IllegalArgumentException("Argument missionGenerator is null.");
		}

		m_transactionWorker = transactionWorker;
		m_now = now;
		m_missionGenerator = missionGenerator;
		m_reset = reset;
	}

	@Override
	public void execute() throws Exception {
		ActorDatasource datasource = new ActorDatasource();
		try (ActorIterator actors = datasource.getActors()) {
			while (actors.hasNext()) {
				m_transactionWorker
						.process(new CreateMission(actors.next().getKey(), m_now, m_missionGenerator, m_reset));
			}
		}
	}

	@Override
	public String toString() {
		return "CreateMissions";
	}
}
