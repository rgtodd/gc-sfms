package sfms.web.models.ajax;

public class MapItemPropertyModel {

	private String m_title;
	private String m_description;
	private String m_url;
	private String m_value;

	public String getTitle() {
		return m_title;
	}

	public void setTitle(String title) {
		m_title = title;
	}

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		m_description = description;
	}

	public String getValue() {
		return m_value;
	}

	public void setValue(String value) {
		m_value = value;
	}

	public String getUrl() {
		return m_url;
	}

	public void setUrl(String url) {
		m_url = url;
	}
}
