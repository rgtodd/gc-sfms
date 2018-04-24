package sfms.web.models.ajax;

import java.util.List;

public class MapItemPropertyGroupModel {

	private String m_title;
	private List<MapItemPropertyModel> m_properties;

	public String getTitle() {
		return m_title;
	}

	public void setTitle(String title) {
		m_title = title;
	}

	public List<MapItemPropertyModel> getProperties() {
		return m_properties;
	}

	public void setProperties(List<MapItemPropertyModel> properties) {
		m_properties = properties;
	}
}
