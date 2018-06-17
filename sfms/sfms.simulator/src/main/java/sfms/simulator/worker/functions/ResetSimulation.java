package sfms.simulator.worker.functions;

import com.google.cloud.datastore.Datastore;

import sfms.db.Db;
import sfms.db.schemas.DbEntity;
import sfms.simulator.worker.WorkerFunction;

public class ResetSimulation implements WorkerFunction {

	private Datastore m_datastore;

	public ResetSimulation(Datastore datastore) {
		if (datastore == null) {
			throw new IllegalArgumentException("Argument datastore is null.");
		}

		m_datastore = datastore;
	}

	@Override
	public void execute() {
		Db.deleteEntities(m_datastore, DbEntity.MissionState.getKind(), null);
		Db.deleteEntities(m_datastore, DbEntity.Mission.getKind(), null);
		Db.deleteEntities(m_datastore, DbEntity.SpaceshipState.getKind(), null);
		Db.deleteEntities(m_datastore, DbEntity.CrewMemberState.getKind(), null);
		Db.deleteEntities(m_datastore, DbEntity.Simulation.getKind(), null);
	}

	@Override
	public String toString() {
		return "ResetSimulation";
	}

}
