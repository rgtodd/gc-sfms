package sfms.web.models;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

public class SimulatorOptionsModel {

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime m_now;
	private Boolean m_reset;

	public LocalDateTime getNow() {
		return m_now;
	}

	public void setNow(LocalDateTime now) {
		m_now = now;
	}

	public Boolean getReset() {
		return m_reset;
	}

	public void setReset(Boolean reset) {
		m_reset = reset;
	}
}
