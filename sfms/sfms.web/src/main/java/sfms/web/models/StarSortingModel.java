package sfms.web.models;

import sfms.web.model.schemas.CrewMemberModelSchema;
import sfms.web.model.schemas.StarModelSchema;

public class StarSortingModel {

	public static final String ASCENDING = "A";
	public static final String DESCENDING = "D";

	private String m_sort;
	private String m_direction;

	public String getSort() {
		return m_sort;
	}

	public void setSort(String sort) {
		m_sort = sort;
	}

	public String getDirection() {
		return m_direction;
	}

	public void setDirection(String direction) {
		m_direction = direction;
	}

	public String getXLinkDirection() {
		return getLinkDirection(StarModelSchema.X);
	}

	public String getYLinkDirection() {
		return getLinkDirection(StarModelSchema.Y);
	}

	public String getZLinkDirection() {
		return getLinkDirection(StarModelSchema.Z);
	}

	public boolean getXHasIcon() {
		return getSort().equals(StarModelSchema.X);
	}

	public boolean getYHasIcon() {
		return getSort().equals(StarModelSchema.Y);
	}

	public boolean getZHasIcon() {
		return getSort().equals(StarModelSchema.Z);
	}

	public String getIconName() {
		if (getDirection().equals(ASCENDING)) {
			return "arrow_drop_up";
		} else {
			return "arrow_drop_down";
		}
	}

	private String getLinkDirection(String column) {
		if (getSort().equals(column)) {
			return toggle(m_direction);
		} else {
			return ASCENDING;
		}
	}

	public String toggle(String direction) {
		if (direction.equals(ASCENDING)) {
			return DESCENDING;
		} else {
			return ASCENDING;
		}
	}

}
