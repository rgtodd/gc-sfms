package sfms.simulator.json;

public class TravelObjectiveDefinition extends ObjectiveDefinition {

	private String m_destinationKeyKind;
	private String m_destinationKeyValue;

	public String getDestinationKeyKind() {
		return m_destinationKeyKind;
	}

	public void setDestinationKeyKind(String destinationKeyKind) {
		m_destinationKeyKind = destinationKeyKind;
	}

	public String getDestinationKeyValue() {
		return m_destinationKeyValue;
	}

	public void setDestinationKeyValue(String destinationKeyValue) {
		m_destinationKeyValue = destinationKeyValue;
	}

	@Override
	public String toString() {
		return "Travel to " + m_destinationKeyKind + "/" + m_destinationKeyValue;
	}
}
