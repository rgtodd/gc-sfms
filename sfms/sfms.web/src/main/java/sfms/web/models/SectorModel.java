package sfms.web.models;

import java.util.List;

public class SectorModel {

	private String m_key;
	private long m_minimumX;
	private long m_minimumY;
	private long m_minimumZ;
	private long m_maximumX;
	private long m_maximumY;
	private long m_maximumZ;
	private List<StarModel> m_stars;

	public SectorModel() {
	}

	public String getKey() {
		return m_key;
	}

	public long getMaximumX() {
		return m_maximumX;
	}

	public long getMaximumY() {
		return m_maximumY;
	}

	public long getMaximumZ() {
		return m_maximumZ;
	}

	public Long getMinimumX() {
		return m_minimumX;
	}

	public long getMinimumY() {
		return m_minimumY;
	}

	public long getMinimumZ() {
		return m_minimumZ;
	}

	public List<StarModel> getStars() {
		return m_stars;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public void setMaximumX(long maximumX) {
		m_maximumX = maximumX;
	}

	public void setMaximumY(long maximumY) {
		m_maximumY = maximumY;
	}

	public void setMaximumZ(long maximumZ) {
		m_maximumZ = maximumZ;
	}

	public void setMinimumX(long minimumX) {
		m_minimumX = minimumX;
	}

	public void setMinimumX(Long minimumX) {
		m_minimumX = minimumX;
	}

	public void setMinimumY(long minimumY) {
		m_minimumY = minimumY;
	}

	public void setMinimumZ(long minimumZ) {
		m_minimumZ = minimumZ;
	}

	public void setStars(List<StarModel> stars) {
		m_stars = stars;
	}
}
