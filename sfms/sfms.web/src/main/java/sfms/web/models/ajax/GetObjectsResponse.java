package sfms.web.models.ajax;

import java.util.List;

public class GetObjectsResponse {

	private List<String> m_objectKeys;
	private List<Double> m_objectPoints;

	public List<String> getObjectKeys() {
		return m_objectKeys;
	}

	public void setObjectKeys(List<String> objectKeys) {
		m_objectKeys = objectKeys;
	}

	public List<Double> getObjectPoints() {
		return m_objectPoints;
	}

	public void setObjectPoints(List<Double> objectPoints) {
		m_objectPoints = objectPoints;
	}
}
