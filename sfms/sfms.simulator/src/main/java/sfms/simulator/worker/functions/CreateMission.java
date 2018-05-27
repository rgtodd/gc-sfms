package sfms.simulator.worker.functions;

import java.time.Instant;

import com.google.cloud.datastore.Datastore;

import sfms.simulator.Actor;
import sfms.simulator.ActorDatasource;
import sfms.simulator.ActorKey;
import sfms.simulator.MissionGenerator;
import sfms.simulator.json.Mission;
import sfms.simulator.worker.WorkerFunction;

public class CreateMission implements WorkerFunction {

	private Datastore m_datastore;
	private ActorKey m_actorKey;
	private Instant m_now;
	private MissionGenerator m_missionGenerator;
	@SuppressWarnings("unused")
	private boolean m_reset;

	public CreateMission(Datastore datastore, ActorKey actorKey, Instant now, MissionGenerator missionGenerator,
			boolean reset) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}
		if (actorKey == null) {
			throw new IllegalArgumentException("Argument actorKey is null.");
		}
		if (now == null) {
			throw new IllegalArgumentException("Argument now is null.");
		}
		if (missionGenerator == null) {
			throw new IllegalArgumentException("Argument missionGenerator is null.");
		}

		m_datastore = datastore;
		m_actorKey = actorKey;
		m_now = now;
		m_missionGenerator = missionGenerator;
		m_reset = reset;
	}

	@Override
	public void execute() {
		ActorDatasource datasource = new ActorDatasource(m_datastore);
		Actor actor = datasource.getActor(m_actorKey);
		Mission mission = m_missionGenerator.createMission(actor);
		actor.assignMission(m_now, mission);
	}

	@Override
	public String toString() {
		return "CreateMission[" + m_actorKey.toString() + "]";
	}

}
