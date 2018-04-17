package sfms.web.models.ajax;

public class Sector {

	private String m_key;
	private Integer m_sx;
	private Integer m_sy;
	private Integer m_sz;
	private Integer m_minimumX;
	private Integer m_minimumY;
	private Integer m_minimumZ;
	private Integer m_maximumX;
	private Integer m_maximumY;
	private Integer m_maximumZ;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public Integer getSx() {
		return m_sx;
	}

	public void setSx(Integer sx) {
		m_sx = sx;
	}

	public Integer getSy() {
		return m_sy;
	}

	public void setSy(Integer sy) {
		m_sy = sy;
	}

	public Integer getSz() {
		return m_sz;
	}

	public void setSz(Integer sz) {
		m_sz = sz;
	}

	public Integer getMinimumX() {
		return m_minimumX;
	}

	public void setMinimumX(Integer minimumX) {
		m_minimumX = minimumX;
	}

	public Integer getMinimumY() {
		return m_minimumY;
	}

	public void setMinimumY(Integer minimumY) {
		m_minimumY = minimumY;
	}

	public Integer getMinimumZ() {
		return m_minimumZ;
	}

	public void setMinimumZ(Integer minimumZ) {
		m_minimumZ = minimumZ;
	}

	public Integer getMaximumX() {
		return m_maximumX;
	}

	public void setMaximumX(Integer maximumX) {
		m_maximumX = maximumX;
	}

	public Integer getMaximumY() {
		return m_maximumY;
	}

	public void setMaximumY(Integer maximumY) {
		m_maximumY = maximumY;
	}

	public Integer getMaximumZ() {
		return m_maximumZ;
	}

	public void setMaximumZ(Integer maximumZ) {
		m_maximumZ = maximumZ;
	}
}
