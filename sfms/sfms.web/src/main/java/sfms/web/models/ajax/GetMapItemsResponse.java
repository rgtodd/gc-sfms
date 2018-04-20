package sfms.web.models.ajax;

import java.util.List;

public class GetMapItemsResponse {

	private List<String> m_mapItemKeys;
	private List<Double> m_mapItemPoints;

	public List<String> getMapItemKeys() {
		return m_mapItemKeys;
	}

	public void setMapItemKeys(List<String> mapItemKeys) {
		m_mapItemKeys = mapItemKeys;
	}

	public List<Double> getMapItemPoints() {
		return m_mapItemPoints;
	}

	public void setMapItemPoints(List<Double> mapItemPoints) {
		m_mapItemPoints = mapItemPoints;
	}
}
