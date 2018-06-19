package sfms.web.models;

import java.time.ZonedDateTime;

public class SimulatorOptionsModel {

	private ZonedDateTime m_now;
	private Integer m_count;
	private Boolean m_reset;

	public ZonedDateTime getNow() {
		return m_now;
	}

	public void setNow(ZonedDateTime now) {
		m_now = now;
	}

	public Integer getCount() {
		return m_count;
	}

	public void setCount(Integer count) {
		m_count = count;
	}

	public Boolean getReset() {
		return m_reset;
	}

	public void setReset(Boolean reset) {
		m_reset = reset;
	}
}
