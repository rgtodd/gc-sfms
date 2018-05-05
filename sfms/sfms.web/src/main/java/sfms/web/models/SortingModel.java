package sfms.web.models;

import sfms.rest.api.SortCriteria;

public class SortingModel {

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

	public String getIconName() {
		if (getDirection().equals(SortCriteria.ASCENDING)) {
			return "arrow_drop_up";
		} else {
			return "arrow_drop_down";
		}
	}

	protected String getLinkDirection(String column) {
		if (getSort().equals(column)) {
			return toggle(m_direction);
		} else {
			return SortCriteria.ASCENDING;
		}
	}

	protected String toggle(String direction) {
		if (direction.equals(SortCriteria.ASCENDING)) {
			return SortCriteria.DESCENDING;
		} else {
			return SortCriteria.ASCENDING;
		}
	}

	public String icon(String field) {
		if (getSort().equals(field)) {
			return "<i class=\"material-icons\">" + getIconName() + "</i>";
		} else {
			return null;
		}
	}

	public String direction(String field) {
		return getLinkDirection(field);
	}
}
