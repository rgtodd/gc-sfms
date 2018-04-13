package sfms.rest.api.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sector {

	private String m_key;
	private long m_minimumX;
	private long m_minimumY;
	private long m_minimumZ;
	private long m_maximumX;
	private long m_maximumY;
	private long m_maximumZ;
	private List<Star> m_stars;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public long getMinimumX() {
		return m_minimumX;
	}

	public void setMinimumX(long minimumX) {
		m_minimumX = minimumX;
	}

	public long getMinimumY() {
		return m_minimumY;
	}

	public void setMinimumY(long minimumY) {
		m_minimumY = minimumY;
	}

	public long getMinimumZ() {
		return m_minimumZ;
	}

	public void setMinimumZ(long minimumZ) {
		m_minimumZ = minimumZ;
	}

	public long getMaximumX() {
		return m_maximumX;
	}

	public void setMaximumX(long maximumX) {
		m_maximumX = maximumX;
	}

	public long getMaximumY() {
		return m_maximumY;
	}

	public void setMaximumY(long maximumY) {
		m_maximumY = maximumY;
	}

	public long getMaximumZ() {
		return m_maximumZ;
	}

	public void setMaximumZ(long maximumZ) {
		m_maximumZ = maximumZ;
	}

	public List<Star> getStars() {
		return m_stars;
	}

	public void setStars(List<Star> stars) {
		m_stars = stars;
	}

}
