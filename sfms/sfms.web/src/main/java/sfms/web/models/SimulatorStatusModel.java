package sfms.web.models;

public class SimulatorStatusModel {

	private String m_controlWorkerStatus;
	private int m_controlWorkerRequestCount;
	private String m_transactionWorkerStatus;
	private int m_transactionWorkerRequestCount;
	private boolean m_canStartControlWorker;
	private boolean m_canStopControlWorker;
	private boolean m_canStartTransactionWorker;
	private boolean m_canStopTransactionWorker;

	public String getControlWorkerStatus() {
		return m_controlWorkerStatus;
	}

	public void setControlWorkerStatus(String controlWorkerStatus) {
		m_controlWorkerStatus = controlWorkerStatus;
	}

	public int getControlWorkerRequestCount() {
		return m_controlWorkerRequestCount;
	}

	public void setControlWorkerRequestCount(int controlWorkerRequestCount) {
		m_controlWorkerRequestCount = controlWorkerRequestCount;
	}

	public String getTransactionWorkerStatus() {
		return m_transactionWorkerStatus;
	}

	public void setTransactionWorkerStatus(String transactionWorkerStatus) {
		m_transactionWorkerStatus = transactionWorkerStatus;
	}

	public int getTransactionWorkerRequestCount() {
		return m_transactionWorkerRequestCount;
	}

	public void setTransactionWorkerRequestCount(int transactionWorkerRequestCount) {
		m_transactionWorkerRequestCount = transactionWorkerRequestCount;
	}

	public boolean isCanStartControlWorker() {
		return m_canStartControlWorker;
	}

	public void setCanStartControlWorker(boolean canStartControlWorker) {
		m_canStartControlWorker = canStartControlWorker;
	}

	public boolean isCanStopControlWorker() {
		return m_canStopControlWorker;
	}

	public void setCanStopControlWorker(boolean canStopControlWorker) {
		m_canStopControlWorker = canStopControlWorker;
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

}
