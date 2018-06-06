package sfms.simulator.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import sfms.db.Db;
import sfms.db.schemas.DbEntity;
import sfms.simulator.Simulation;
import sfms.simulator.api.models.SimulatorStatus;
import sfms.simulator.api.models.WorkerStatus;
import sfms.simulator.worker.Worker;

/**
 * Controller for the Admin REST service.
 * 
 * These operations administrate the simulator server.
 */
@RestController
@RequestMapping("/admin")
public class AdminRestController {

	@Autowired
	private Worker controlWorker;

	@Autowired
	private Worker transactionWorker;

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AdminRestController.class.getName());

	@GetMapping(value = "/status")
	public SimulatorStatus getStatus() {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Simulation simulation = Simulation.getCurrentSimulation(datastore);

		SimulatorStatus response = new SimulatorStatus();
		response.setControlWorkerStatus(getControlWorkerStatus());
		response.setTransactionWorkerStatus(getTransactionWorkerStatus());
		if (simulation != null) {
			response.setSimulationInstant(simulation.getTimestamp());
		}

		return response;
	}

	@GetMapping(value = "/worker/control/status")
	public WorkerStatus getControlWorkerStatus() {

		WorkerStatus response = new WorkerStatus();
		response.setStatus(controlWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE);
		response.setRequestCount(controlWorker.getRequestCount());

		return response;
	}

	@PostMapping(value = "/worker/control/status")
	public WorkerStatus setControlWorkerStatus(@RequestBody String status) {

		if (status.equals(WorkerStatus.ACTIVE)) {
			if (!controlWorker.isActive()) {
				controlWorker.start();
			}
		} else if (status.equals(WorkerStatus.INACTIVE)) {
			if (controlWorker.isActive()) {
				controlWorker.stop();
			}
		}

		return getControlWorkerStatus();
	}

	@GetMapping(value = "/worker/transaction/status")
	public WorkerStatus getTransactionWorkerStatus() {

		WorkerStatus response = new WorkerStatus();
		response.setStatus(transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE);
		response.setRequestCount(transactionWorker.getRequestCount());

		return response;
	}

	@PostMapping(value = "/worker/transaction/status")
	public WorkerStatus setTransactionWorkerStatus(@RequestBody String status) {

		if (status.equals(WorkerStatus.ACTIVE)) {
			if (!transactionWorker.isActive()) {
				transactionWorker.start();
			}
		} else if (status.equals(WorkerStatus.INACTIVE)) {
			if (transactionWorker.isActive()) {
				transactionWorker.stop();
			}
		}

		return getTransactionWorkerStatus();
	}

	@PostMapping(value = "/resetSimulation")
	public void resetSimulation() {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Db.deleteEntities(datastore, DbEntity.MissionState.getKind(), null);
		Db.deleteEntities(datastore, DbEntity.Mission.getKind(), null);
		Db.deleteEntities(datastore, DbEntity.SpaceshipState.getKind(), null);
		Db.deleteEntities(datastore, DbEntity.CrewMemberState.getKind(), null);
		Db.deleteEntities(datastore, DbEntity.Simulation.getKind(), null);

	}
}
