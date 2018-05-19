package sfms.simulation;

import java.time.Instant;
import java.util.logging.Logger;

import sfms.simulation.json.Mission;

public class Simulator {

	private final Logger logger = Logger.getLogger(Simulator.class.getName());

	private ActorDatasource m_actorDatasource;
	private MissionGenerator m_missionGenerator;

	public Simulator() {
		m_actorDatasource = new ActorDatasource();
		m_missionGenerator = new MissionGenerator();
	}

	public void processActors(Instant now) {

		try (ActorIterator actorIterator = m_actorDatasource.getActors()) {
			while (actorIterator.hasNext()) {
				Actor actor = actorIterator.next();

				Mission mission = m_missionGenerator.createMission(actor);
				// if (mission != null) {
				// actor.assignMission(mission);
				// }

				logger.info("Simulation complete:  " + actor.getEntityKey().toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
