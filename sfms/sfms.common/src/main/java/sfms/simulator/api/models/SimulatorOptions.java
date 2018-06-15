package sfms.simulator.api.models;

import java.time.Instant;

public class SimulatorOptions {

	private Instant m_now;
	private Boolean m_reset;
	private Integer m_count;

	public Instant getNow() {
		return m_now;
	}

	public void setNow(Instant now) {
		m_now = now;
	}

	public Boolean getReset() {
		return m_reset;
	}

	public void setReset(Boolean reset) {
		m_reset = reset;
	}

	public Integer getCount() {
		return m_count;
	}

	public void setCount(Integer count) {
		m_count = count;
	}

}
