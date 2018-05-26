package sfms.simulator.worker.functions;

import java.time.Instant;

import sfms.simulator.ActorKey;
import sfms.simulator.worker.WorkerFunction;

public class InitializeActor implements WorkerFunction {

	private ActorKey m_actorKey;
	@SuppressWarnings("unused")
	private Instant m_now;

	public InitializeActor(ActorKey actorKey, Instant now) {
		if (actorKey == null) {
			throw new IllegalArgumentException("Argument actorKey is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}

		m_actorKey = actorKey;
		m_now = now;
	}

	@Override
	public void execute() {
		// logger.info("Processing " + m_actorKey.toString());
	}

	@Override
	public String toString() {
		return "InitializeActor[" + m_actorKey.toString() + "]";
	}

}
