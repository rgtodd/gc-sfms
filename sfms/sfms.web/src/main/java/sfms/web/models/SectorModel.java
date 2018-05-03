package sfms.web.models;

import java.util.List;

public class SectorModel {

	private String m_key;
	private Long m_sectorX;
	private Long m_sectorY;
	private Long m_sectorZ;
	private Long m_minimumX;
	private Long m_minimumY;
	private Long m_minimumZ;
	private Long m_maximumX;
	private Long m_maximumY;
	private Long m_maximumZ;
	private List<StarModel> m_stars;

	public SectorModel() {
	}

	public String getKey() {
		return m_key;
	}

	public Long getSectorX() {
		return m_sectorX;
	}

	public void setSectorX(Long sectorX) {
		m_sectorX = sectorX;
	}

	public Long getSectorY() {
		return m_sectorY;
	}

	public void setSectorY(Long sectorY) {
		m_sectorY = sectorY;
	}

	public Long getSectorZ() {
		return m_sectorZ;
	}

	public void setSectorZ(Long sectorZ) {
		m_sectorZ = sectorZ;
	}

	public Long getMaximumX() {
		return m_maximumX;
	}

	public Long getMaximumY() {
		return m_maximumY;
	}

	public Long getMaximumZ() {
		return m_maximumZ;
	}

	public Long getMinimumX() {
		return m_minimumX;
	}

	public Long getMinimumY() {
		return m_minimumY;
	}

	public Long getMinimumZ() {
		return m_minimumZ;
	}

	public List<StarModel> getStars() {
		return m_stars;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public void setMaximumX(Long maximumX) {
		m_maximumX = maximumX;
	}

	public void setMaximumY(Long maximumY) {
		m_maximumY = maximumY;
	}

	public void setMaximumZ(Long maximumZ) {
		m_maximumZ = maximumZ;
	}

	public void setMinimumX(Long minimumX) {
		m_minimumX = minimumX;
	}

	public void setMinimumY(Long minimumY) {
		m_minimumY = minimumY;
	}

	public void setMinimumZ(Long minimumZ) {
		m_minimumZ = minimumZ;
	}

	public void setStars(List<StarModel> stars) {
		m_stars = stars;
	}
}
