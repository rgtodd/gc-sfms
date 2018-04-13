package sfms.web.models;

import java.util.List;

public class ClusterModel {

	private String m_key;
	private Long m_minimumX;
	private List<StarModel> m_stars;

	public ClusterModel() {
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public Long getMinimumX() {
		return m_minimumX;
	}

	public void setMinimumX(Long minimumX) {
		m_minimumX = minimumX;
	}

	public List<StarModel> getStars() {
		return m_stars;
	}

	public void setStars(List<StarModel> stars) {
		m_stars = stars;
	}
}
