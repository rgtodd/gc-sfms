package sfms.simulator.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sfms.simulator.api.models.SimulatorStatus;
import sfms.simulator.api.schemas.WorkerStatus;
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

		SimulatorStatus response = new SimulatorStatus();
		response.setJobWorkerStatus(controlWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE);
		response.setTransactionWorkerStatus(
				transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE);

		return response;
	}

	@GetMapping(value = "/worker/job/status")
	public String getJobWorkerStatus() {
		return controlWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}

	@PostMapping(value = "/worker/job/status")
	public String setJobWorkerStatus(@RequestBody String status) {

		if (status.equals(WorkerStatus.ACTIVE)) {
			if (!controlWorker.isActive()) {
				controlWorker.start();
			}
		} else if (status.equals(WorkerStatus.INACTIVE)) {
			if (controlWorker.isActive()) {
				controlWorker.stop();
			}
		}

		return controlWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}

	@GetMapping(value = "/worker/transaction/status")
	public String getTransactionWorkerStatus() {
		return transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}

	@PostMapping(value = "/worker/transaction/status")
	public String setTransactionWorkerStatus(@RequestBody String status) {

		if (status.equals(WorkerStatus.ACTIVE)) {
			if (!transactionWorker.isActive()) {
				transactionWorker.start();
			}
		} else if (status.equals(WorkerStatus.INACTIVE)) {
			if (transactionWorker.isActive()) {
				transactionWorker.stop();
			}
		}

		return transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}
}
