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
import sfms.simulator.worker.ControlWorker;

/**
 * Controller for the Admin REST service.
 * 
 * These operations administrate the simulator server.
 */
@RestController
@RequestMapping("/admin")
public class AdminRestController {

	@Autowired
	private ControlWorker m_jobWorker;

	@Autowired
	private ControlWorker m_transactionWorker;

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AdminRestController.class.getName());

	@GetMapping(value = "/status")
	public SimulatorStatus getStatus() {

		SimulatorStatus response = new SimulatorStatus();
		response.setJobWorkerStatus(m_jobWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE);
		response.setTransactionWorkerStatus(
				m_transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE);

		return response;
	}

	@GetMapping(value = "/worker/job/status")
	public String getJobWorkerStatus() {
		return m_jobWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}

	@PostMapping(value = "/worker/job/status")
	public String setJobWorkerStatus(@RequestBody String status) {

		if (status.equals(WorkerStatus.ACTIVE)) {
			if (!m_jobWorker.isActive()) {
				m_jobWorker.start();
			}
		} else if (status.equals(WorkerStatus.INACTIVE)) {
			if (m_jobWorker.isActive()) {
				m_jobWorker.stop();
			}
		}

		return m_jobWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}

	@GetMapping(value = "/worker/transaction/status")
	public String getTransactionWorkerStatus() {
		return m_transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}

	@PostMapping(value = "/worker/transaction/status")
	public String setTransactionWorkerStatus(@RequestBody String status) {

		if (status.equals(WorkerStatus.ACTIVE)) {
			if (!m_transactionWorker.isActive()) {
				m_transactionWorker.start();
			}
		} else if (status.equals(WorkerStatus.INACTIVE)) {
			if (m_transactionWorker.isActive()) {
				m_transactionWorker.stop();
			}
		}

		return m_transactionWorker.isActive() ? WorkerStatus.ACTIVE : WorkerStatus.INACTIVE;
	}
}
