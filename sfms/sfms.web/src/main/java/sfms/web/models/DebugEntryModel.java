package sfms.web.models;

public class DebugEntryModel {

	private String m_id;
	private String m_value;

	public String getId() {
		return m_id;
	}

	public void setId(String id) {
		m_id = id;
	}

	public String getValue() {
		return m_value;
	}

	public void setValue(String value) {
		m_value = value;
	}

	@Override
	public String toString() {
		return "Id = " + getId() + ", Value = " + getValue();
	}
}
