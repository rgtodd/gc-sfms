package sfms.simulator;

import java.time.Instant;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;

import sfms.simulator.json.Mission;

public class Simulator {

	private final Logger logger = Logger.getLogger(Simulator.class.getName());

	private Datastore m_datastore;

	private ActorDatasource m_actorDatasource;
	private MissionGenerator m_missionGenerator;

	public Simulator(Datastore datastore) {
		m_datastore = datastore;

		m_actorDatasource = new ActorDatasource(m_datastore);
		m_missionGenerator = new MissionGenerator();
	}

	public void processActors(Instant now) {

		try (ActorIterator actorIterator = m_actorDatasource.getActors()) {
			while (actorIterator.hasNext()) {
				Actor actor = actorIterator.next();

				@SuppressWarnings("unused")
				Mission mission = m_missionGenerator.createMission(actor);
				// if (mission != null) {
				// actor.assignMission(mission);
				// }

				logger.info("Simulation complete:  " + actor.getActorKey().toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
