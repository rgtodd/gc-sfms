package sfms.web.models;

import sfms.web.model.schemas.CrewMemberModelSchema;

public class CrewMemberSortingModel {

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

	public String getFirstNameLinkDirection() {
		return getLinkDirection(CrewMemberModelSchema.FIRST_NAME);
	}

	public String getLastNameLinkDirection() {
		return getLinkDirection(CrewMemberModelSchema.LAST_NAME);
	}

	public boolean getFirstNameHasIcon() {
		return getSort().equals(CrewMemberModelSchema.FIRST_NAME);
	}

	public boolean getLastNameHasIcon() {
		return getSort().equals(CrewMemberModelSchema.LAST_NAME);
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
