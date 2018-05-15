package sfms.simulation;

import java.time.Instant;

import sfms.simulation.json.Mission;

public class Simulator {

	private ActorDatasource m_actorDatasource;
	private MissionGenerator m_missionGenerator;

	public void processActiveActors(Instant now) {

		try (ActorIterator actorIterator = m_actorDatasource.getActiveActors()) {
			while (actorIterator.hasNext()) {
				Actor actor = actorIterator.next();
				actor.updateStatus(now);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processInactiveActors(Instant now) {

		try (ActorIterator actorIterator = m_actorDatasource.getInactiveActors()) {
			while (actorIterator.hasNext()) {
				Actor actor = actorIterator.next();
				Mission mission = m_missionGenerator.createMission(actor);
				actor.assignMission(mission);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
