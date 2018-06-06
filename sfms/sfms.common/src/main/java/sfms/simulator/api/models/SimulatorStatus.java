package sfms.simulator.api.models;

import java.time.Instant;

public class SimulatorStatus {

	private WorkerStatus m_controlWorkerStatus;
	private WorkerStatus m_transactionWorkerStatus;
	private Instant m_simulationInstant;

	public WorkerStatus getControlWorkerStatus() {
		return m_controlWorkerStatus;
	}

	public void setControlWorkerStatus(WorkerStatus controlWorkerStatus) {
		m_controlWorkerStatus = controlWorkerStatus;
	}

	public WorkerStatus getTransactionWorkerStatus() {
		return m_transactionWorkerStatus;
	}

	public void setTransactionWorkerStatus(WorkerStatus transactionWorkerStatus) {
		m_transactionWorkerStatus = transactionWorkerStatus;
	}

	public Instant getSimulationInstant() {
		return m_simulationInstant;
	}

	public void setSimulationInstant(Instant simulationInstant) {
		m_simulationInstant = simulationInstant;
	}

}
