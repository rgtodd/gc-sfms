package sfms.web.models;

import java.util.List;

public class ClusterModel {

	private String m_key;
	private Long m_clusterPartition;
	private Long m_clusterX;
	private Long m_clusterY;
	private Long m_clusterZ;
	private Long m_minimumX;
	private Long m_minimumY;
	private Long m_minimumZ;
	private Long m_maximumX;
	private Long m_maximumY;
	private Long m_maximumZ;
	private List<StarModel> m_stars;

	public ClusterModel() {
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public Long getClusterPartition() {
		return m_clusterPartition;
	}

	public void setClusterPartition(Long clusterPartition) {
		m_clusterPartition = clusterPartition;
	}

	public Long getClusterX() {
		return m_clusterX;
	}

	public void setClusterX(Long clusterX) {
		m_clusterX = clusterX;
	}

	public Long getClusterY() {
		return m_clusterY;
	}

	public void setClusterY(Long clusterY) {
		m_clusterY = clusterY;
	}

	public Long getClusterZ() {
		return m_clusterZ;
	}

	public void setClusterZ(Long clusterZ) {
		m_clusterZ = clusterZ;
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

	public List<StarModel> getStars() {
		return m_stars;
	}

	public void setStars(List<StarModel> stars) {
		m_stars = stars;
	}

	public String getClusterXYZ() {
		return ModuleUtility.formatCoordinates(m_clusterX, m_clusterY, m_clusterZ);
	}

	public String getMinimumXYZ() {
		return ModuleUtility.formatCoordinates(m_minimumX, m_minimumY, m_minimumZ);
	}

	public String getMaximumXYZ() {
		return ModuleUtility.formatCoordinates(m_maximumX, m_maximumY, m_maximumZ);
	}
}
