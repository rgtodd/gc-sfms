package sfms.web.models.ajax;

import java.util.List;

public class MapItemModel {

	private String m_sectorKey;
	private String m_mapItemKey;
	private Integer m_mapItemType;
	private String m_mapItemName;
	private List<MapItemPropertyGroupModel> m_propertyGroups;

	public String getSectorKey() {
		return m_sectorKey;
	}

	public void setSectorKey(String sectorKey) {
		m_sectorKey = sectorKey;
	}

	public String getMapItemKey() {
		return m_mapItemKey;
	}

	public void setMapItemKey(String mapItemKey) {
		m_mapItemKey = mapItemKey;
	}

	public Integer getMapItemType() {
		return m_mapItemType;
	}

	public void setMapItemType(Integer mapItemType) {
		m_mapItemType = mapItemType;
	}

	public List<MapItemPropertyGroupModel> getPropertyGroups() {
		return m_propertyGroups;
	}

	public void setPropertyGroups(List<MapItemPropertyGroupModel> propertyGroups) {
		m_propertyGroups = propertyGroups;
	}

	public String getMapItemName() {
		return m_mapItemName;
	}

	public void setMapItemName(String mapItemName) {
		m_mapItemName = mapItemName;
	}
}
