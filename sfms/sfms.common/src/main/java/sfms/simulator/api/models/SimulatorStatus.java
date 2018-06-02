package sfms.simulator.api.models;

import java.time.Instant;

public class SimulatorStatus {

	private String m_controlWorkerStatus;
	private String m_transactionWorkerStatus;
	private Instant m_simulationInstant;

	public String getControlWorkerStatus() {
		return m_controlWorkerStatus;
	}

	public void setControlWorkerStatus(String controlWorkerStatus) {
		m_controlWorkerStatus = controlWorkerStatus;
	}

	public String getTransactionWorkerStatus() {
		return m_transactionWorkerStatus;
	}

	public void setTransactionWorkerStatus(String transactionWorkerStatus) {
		m_transactionWorkerStatus = transactionWorkerStatus;
	}

	public Instant getSimulationInstant() {
		return m_simulationInstant;
	}

	public void setSimulationInstant(Instant simulationInstant) {
		m_simulationInstant = simulationInstant;
	}

}
