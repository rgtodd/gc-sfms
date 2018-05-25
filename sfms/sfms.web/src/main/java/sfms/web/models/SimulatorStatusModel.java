package sfms.web.models;

public class SimulatorStatusModel {

	private String m_jobWorkerStatus;
	private String m_transactionWorkerStatus;
	private boolean m_canStartJobWorker;
	private boolean m_canStopJobWorker;
	private boolean m_canStartTransactionWorker;
	private boolean m_canStopTransactionWorker;

	public boolean isCanStartJobWorker() {
		return m_canStartJobWorker;
	}

	public void setCanStartJobWorker(boolean canStartJobWorker) {
		m_canStartJobWorker = canStartJobWorker;
	}

	public boolean isCanStopJobWorker() {
		return m_canStopJobWorker;
	}

	public void setCanStopJobWorker(boolean canStopJobWorker) {
		m_canStopJobWorker = canStopJobWorker;
	}

	public boolean isCanStartTransactionWorker() {
		return m_canStartTransactionWorker;
	}

	public void setCanStartTransactionWorker(boolean canStartTransactionWorker) {
		m_canStartTransactionWorker = canStartTransactionWorker;
	}

	public boolean isCanStopTransactionWorker() {
		return m_canStopTransactionWorker;
	}

	public void setCanStopTransactionWorker(boolean canStopTransactionWorker) {
		m_canStopTransactionWorker = canStopTransactionWorker;
	}

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
