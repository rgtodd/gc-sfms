package sfms.simulator.json;

import java.time.Duration;

public class WaitObjectiveDefinition extends ObjectiveDefinition {

	private Duration m_waitDuration;

	public Duration getWaitDuration() {
		return m_waitDuration;
	}

	public void setWaitDuration(Duration waitDuration) {
		m_waitDuration = waitDuration;
	}

	@Override
	public String toString() {
		long days = m_waitDuration.toDays();
		if (days == 1) {
			return "Wait 1 day";
		} else {
			return "Wait " + days + " days";
		}
	}

}
