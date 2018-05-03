package sfms.rest.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Defines a Spaceship entity exposed by the Spaceship REST service.
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Spaceship {

	private String m_key;
	private String m_name;
	private Long m_x;
	private Long m_y;
	private Long m_z;
	private String m_starKey;

	public String getKey() {
		return m_key;
	}

	public void setKey(String key) {
		m_key = key;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public Long getX() {
		return m_x;
	}

	public void setX(Long x) {
		m_x = x;
	}

	public Long getY() {
		return m_y;
	}

	public void setY(Long y) {
		m_y = y;
	}

	public Long getZ() {
		return m_z;
	}

	public void setZ(Long z) {
		m_z = z;
	}

	public String getStarKey() {
		return m_starKey;
	}

	public void setStarKey(String starKey) {
		m_starKey = starKey;
	}

	@Override
	public String toString() {
		return "Id = " + getKey() + ", Name = " + getName();
	}
}
