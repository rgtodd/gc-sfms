package sfms.web.models.ajax;

import java.util.List;

public class GetSectorsResponse {

	public List<SectorModel> m_sectors;

	public List<SectorModel> getSectors() {
		return m_sectors;
	}

	public void setSectors(List<SectorModel> sectors) {
		m_sectors = sectors;
	}
}
