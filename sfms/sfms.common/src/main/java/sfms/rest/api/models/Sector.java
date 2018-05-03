package sfms.rest.api.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Sector entity exposed by the Sector REST service.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sector {

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
	private List<Star> m_stars;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
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

	public Long getMinimumX() {
		return m_minimumX;
	}

	public void setMinimumX(Long minimumX) {
		m_minimumX = minimumX;
	}

	public Long getMinimumY() {
		return m_minimumY;
	}

	public void setMinimumY(Long minimumY) {
		m_minimumY = minimumY;
	}

	public Long getMinimumZ() {
		return m_minimumZ;
	}

	public void setMinimumZ(Long minimumZ) {
		m_minimumZ = minimumZ;
	}

	public Long getMaximumX() {
		return m_maximumX;
	}

	public void setMaximumX(Long maximumX) {
		m_maximumX = maximumX;
	}

	public Long getMaximumY() {
		return m_maximumY;
	}

	public void setMaximumY(Long maximumY) {
		m_maximumY = maximumY;
	}

	public Long getMaximumZ() {
		return m_maximumZ;
	}

	public void setMaximumZ(Long maximumZ) {
		m_maximumZ = maximumZ;
	}

	public List<Star> getStars() {
		return m_stars;
	}

	public void setStars(List<Star> stars) {
		m_stars = stars;
	}

}
