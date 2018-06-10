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
		return "Wait " + m_waitDuration.toString();
	}

}
