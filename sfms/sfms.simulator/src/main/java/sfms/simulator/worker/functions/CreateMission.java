package sfms.simulator.worker.functions;

import java.time.Instant;

import sfms.simulator.Actor;
import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorKey;
import sfms.simulator.MissionGenerator;
import sfms.simulator.json.Mission;
import sfms.simulator.worker.WorkerFunction;

public class CreateMission implements WorkerFunction {

	private ActorKey m_actorKey;
	private Instant m_now;
	private MissionGenerator m_missionGenerator;
	@SuppressWarnings("unused")
	private boolean m_reset;

	public CreateMission(ActorKey actorKey, Instant now, MissionGenerator missionGenerator, boolean reset) {
		if (actorKey == null) {
			throw new IllegalArgumentException("Argument actorKey is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}
		if (missionGenerator == null) {
			throw new IllegalArgumentException("Argument missionGenerator is null.");
		}

		m_actorKey = actorKey;
		m_now = now;
		m_missionGenerator = missionGenerator;
		m_reset = reset;
	}

	@Override
	public void execute() {
		ActorDatasource datasource = new ActorDatasource();
		Actor actor = datasource.getActor(m_actorKey);
		Mission mission = m_missionGenerator.createMission(actor);
		actor.assignMission(m_now, mission);
	}

	@Override
	public String toString() {
		return "CreateMission[" + m_actorKey.toString() + "]";
	}

}
