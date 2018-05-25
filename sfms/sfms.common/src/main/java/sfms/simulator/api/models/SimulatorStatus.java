package sfms.simulator.api.models;

public class SimulatorStatus {

	private String m_jobWorkerStatus;
	private String m_transactionWorkerStatus;

	public String getJobWorkerStatus() {
		return m_jobWorkerStatus;
	}

	public void setJobWorkerStatus(String jobWorkerStatus) {
		m_jobWorkerStatus = jobWorkerStatus;
	}

	public String getTransactionWorkerStatus() {
		return m_transactionWorkerStatus;
	}

	public void setTransactionWorkerStatus(String transactionWorkerStatus) {
		m_transactionWorkerStatus = transactionWorkerStatus;
	}

}
