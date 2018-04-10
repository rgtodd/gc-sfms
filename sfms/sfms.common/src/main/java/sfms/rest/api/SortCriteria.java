package sfms.rest.api;

import java.util.ArrayList;
import java.util.List;

public class SortCriteria {

	private static final char SPACE = ' ';
	private static final String EMPTY_STRING = "";
	private static final String DELIMITER = ",";
	private static final String DELIMITER_REGEX = DELIMITER;
	private static final String DESCENDING = "descending";

	private List<String> m_columns;
	private List<Boolean> m_directions;

	private SortCriteria(List<String> columns, List<Boolean> directions) {
		m_columns = columns;
		m_directions = directions;
	}

	public static SortCriteria ascending(String column) {
		return SortCriteria.parse(column);
	}

	public static SortCriteria descending(String column) {
		return SortCriteria.parse(column + SPACE + DESCENDING);
	}

	public static SortCriteria parse(String text) {
		if (text == null)
			return null;

		String[] fields = text.split(DELIMITER_REGEX);

		List<String> columns = new ArrayList<String>(fields.length);
		List<Boolean> directions = new ArrayList<Boolean>(fields.length);

		for (int idx = 0; idx < fields.length; ++idx) {
			String field = fields[idx];

			int idxSpace = field.indexOf(SPACE);
			if (idxSpace == -1) {
				columns.add(field);
				directions.add(false);
			} else {
				String column = field.substring(0, idxSpace);
				String descending = field.substring(idxSpace + 1);

				columns.add(column);
				directions.add(DESCENDING.startsWith(descending.toLowerCase()));
			}
		}

		return new SortCriteria(columns, directions);
	}

	public int size() {
		if (m_columns == null) {
			return 0;
		} else {
			return m_columns.size();
		}
	}

	public String getColumn(int index) {
		return m_columns.get(index);
	}

	public boolean getDescending(int index) {
		return m_directions.get(index);
	}

	public String toString() {
		if (m_columns == null) {
			return EMPTY_STRING;
		} else {
			StringBuilder sb = new StringBuilder();

			String prefix = EMPTY_STRING;
			for (int idx = 0; idx < size(); ++idx) {
				sb.append(prefix);
				prefix = DELIMITER;

				sb.append(getColumn(idx));
				if (getDescending(idx)) {
					sb.append(" ");
					sb.append(DESCENDING);
				}
			}

			return sb.toString();
		}
	}
}
