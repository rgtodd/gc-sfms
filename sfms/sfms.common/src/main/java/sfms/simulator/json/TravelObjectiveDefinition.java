package sfms.simulator.json;

public class TravelObjectiveDefinition extends ObjectiveDefinition {

	private String m_starKey;

	public String getStarKey() {
		return m_starKey;
	}

	public void setStarKey(String starKey) {
		m_starKey = starKey;
	}

	@Override
	public String toString() {
		return "Travel to star " + m_starKey;
	}
}
