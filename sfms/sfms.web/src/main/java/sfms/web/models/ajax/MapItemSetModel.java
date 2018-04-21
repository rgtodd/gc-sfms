package sfms.web.models.ajax;

import java.util.List;

public class MapItemSetModel {

	private String m_sectorKey;
	private Integer m_mapItemType;
	private List<String> m_mapItemKeys;
	private List<Double> m_mapItemPoints;

	public String getSectorKey() {
		return m_sectorKey;
	}

	public void setSectorKey(String sectorKey) {
		m_sectorKey = sectorKey;
	}

	public Integer getMapItemType() {
		return m_mapItemType;
	}

	public void setMapItemType(Integer mapItemType) {
		m_mapItemType = mapItemType;
	}

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
