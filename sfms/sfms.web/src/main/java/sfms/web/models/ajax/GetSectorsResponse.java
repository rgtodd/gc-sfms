package sfms.web.models.ajax;

import java.util.List;

public class GetSectorsResponse {

	public List<Sector> m_sectors;

	public List<Sector> getSectors() {
		return m_sectors;
	}

	public void setSectors(List<Sector> sectors) {
		m_sectors = sectors;
	}
}
