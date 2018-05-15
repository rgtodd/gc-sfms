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

		logger.info("Simulator::processActors - Starting");

		try (ActorIterator actorIterator = m_actorDatasource.getActors()) {
			while (actorIterator.hasNext()) {

				Actor actor = actorIterator.next();
				logger.info("Retrieved actor " + actor.getEntityKey().toString());

				Mission mission = m_missionGenerator.createMission(actor);
				if (mission != null) {
					actor.assignMission(mission);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Simulator::processActors - Complete");
	}
}
