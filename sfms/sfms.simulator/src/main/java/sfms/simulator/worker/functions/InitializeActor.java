package sfms.simulator.worker.functions;

import java.time.Instant;

import sfms.simulator.Actor;
import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorKey;
import sfms.simulator.worker.WorkerFunction;

public class InitializeActor implements WorkerFunction {

	private ActorKey m_actorKey;
	private Instant m_now;
	private boolean m_reset;

	public InitializeActor(ActorKey actorKey, Instant now, boolean reset) {
		if (actorKey == null) {
			throw new IllegalArgumentException("Argument actorKey is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}

		m_actorKey = actorKey;
		m_now = now;
		m_reset = reset;
	}

	@Override
	public void execute() {
		ActorDatasource datasource = new ActorDatasource();
		Actor actor = datasource.getActor(m_actorKey);
		actor.initialize(m_now, m_reset);
	}

	@Override
	public String toString() {
		return "InitializeActor[" + m_actorKey.toString() + "]";
	}

}
