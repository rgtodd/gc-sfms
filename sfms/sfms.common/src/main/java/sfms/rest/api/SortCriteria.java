package sfms.rest.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the sort criteria used by REST service search methods.
 * 
 * REST service callers can use {@link #newBuilder()} to construct a new
 * SortCriteria object. Once the object has been created, call
 * {@link #toString()} to create the value to be passed to the service. The
 * resulting value should be URL encoded.
 * 
 * REST services can reconstruct the SortCriteria object using the
 * {@link #parse(String)} function.
 *
 */
public class SortCriteria {

	public static final String ASCENDING = "asc";
	public static final String DESCENDING = "des";

	private static final char SPACE = ' ';
	private static final String EMPTY_STRING = "";
	private static final String DELIMITER = ",";
	private static final String DELIMITER_REGEX = DELIMITER;

	private List<String> m_columns;
	private List<String> m_directions;

	private SortCriteria(List<String> columns, List<String> directions) {
		m_columns = columns;
		m_directions = directions;
	}

	public static SortCriteria parse(String text) {
		if (text == null)
			return null;

		String[] fields = text.split(DELIMITER_REGEX);

		List<String> columns = new ArrayList<String>(fields.length);
		List<String> directions = new ArrayList<String>(fields.length);

		for (int idx = 0; idx < fields.length; ++idx) {
			String field = fields[idx];

			int idxSpace = field.indexOf(SPACE);
			if (idxSpace == -1) {
				columns.add(field);
				directions.add(ASCENDING);
			} else {
				String column = field.substring(0, idxSpace);
				String descending = field.substring(idxSpace + 1);

				columns.add(column);
				directions.add(descending);
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

	public String getDirection(int index) {
		return m_directions.get(index);
	}

	public boolean isAscending(int index) {
		return m_directions.get(index).equals(ASCENDING);
	}

	public boolean isDescending(int index) {
		return m_directions.get(index).equals(DESCENDING);
	}

	@Override
	public String toString() {
		if (m_columns == null || m_columns.isEmpty()) {
			return EMPTY_STRING;
		} else {
			StringBuilder sb = new StringBuilder();

			String prefix = EMPTY_STRING;
			for (int idx = 0; idx < size(); ++idx) {
				sb.append(prefix);
				prefix = DELIMITER;

				sb.append(getColumn(idx));
				sb.append(SPACE);
				sb.append(getDirection(idx));
			}

			return sb.toString();
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private List<String> m_columns = new ArrayList<String>();
		private List<String> m_directions = new ArrayList<String>();

		private Builder() {
		}

		public Builder ascending(String column) {
			m_columns.add(column);
			m_directions.add(ASCENDING);

			return this;
		}

		public Builder descending(String column) {
			m_columns.add(column);
			m_directions.add(DESCENDING);

			return this;
		}

		public SortCriteria build() {
			return new SortCriteria(m_columns, m_directions);
		}
	}
}
