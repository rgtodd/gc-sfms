package sfms.simulator.api.models;

public class WorkerStatus {

	public static final String ACTIVE = "ACTIVE";
	public static final String INACTIVE = "INACTIVE";

	private String m_status;
	private int m_requestCount;

	public String getStatus() {
		return m_status;
	}

	public void setStatus(String status) {
		m_status = status;
	}

	public int getRequestCount() {
		return m_requestCount;
	}

	public void setRequestCount(int requestCount) {
		m_requestCount = requestCount;
	}

}
