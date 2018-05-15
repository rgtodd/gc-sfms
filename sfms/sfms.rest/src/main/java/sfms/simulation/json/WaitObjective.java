package sfms.simulation.json;

import java.time.Duration;

public class WaitObjective extends Objective {

	private Duration m_waitDuration;

	public Duration getWaitDuration() {
		return m_waitDuration;
	}

	public void setWaitDuration(Duration waitDuration) {
		m_waitDuration = waitDuration;
	}
}
