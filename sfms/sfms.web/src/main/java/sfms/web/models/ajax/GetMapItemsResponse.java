package sfms.web.models.ajax;

import java.util.List;

public class GetMapItemsResponse {

	private List<MapItemSetModel> m_mapItemSets;

	public List<MapItemSetModel> getMapItemSets() {
		return m_mapItemSets;
	}

	public void setMapItemSets(List<MapItemSetModel> mapItemSets) {
		m_mapItemSets = mapItemSets;
	}
}
