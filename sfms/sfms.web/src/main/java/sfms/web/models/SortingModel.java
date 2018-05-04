package sfms.web.models;

public class SortingModel {

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

	public String getIconName() {
		if (getDirection().equals(ASCENDING)) {
			return "arrow_drop_up";
		} else {
			return "arrow_drop_down";
		}
	}

	protected String getLinkDirection(String column) {
		if (getSort().equals(column)) {
			return toggle(m_direction);
		} else {
			return ASCENDING;
		}
	}

	protected String toggle(String direction) {
		if (direction.equals(ASCENDING)) {
			return DESCENDING;
		} else {
			return ASCENDING;
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
