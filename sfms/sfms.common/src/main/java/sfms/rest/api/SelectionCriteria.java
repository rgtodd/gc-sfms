package sfms.rest.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the selection criteria used by REST service search methods.
 * 
 * REST service callers can use {@link #newBuilder()} to construct a new
 * SelectCriteria object. Once the object has been created, call
 * {@link #toString()} to create the value to be passed to the service. The
 * resulting value should be URL encoded.
 * 
 * REST services can reconstruct the SelectCriteria object using the
 * {@link #parse(String)} function.
 *
 */
public class SelectionCriteria {

	private static final String EMPTY_STRING = "";
	private static final String DELIMITER = ",";
	private static final String DELIMITER_REGEX = DELIMITER;

	private List<String> m_columns;

	private SelectionCriteria(List<String> columns) {
		m_columns = columns;
	}

	public static SelectionCriteria parse(String text) {
		if (text == null) {
			return null;
		}

		String[] fields = text.split(DELIMITER_REGEX);

		List<String> columns = new ArrayList<String>(fields.length);

		for (int idx = 0; idx < fields.length; ++idx) {
			String field = fields[idx];
			columns.add(field);
		}

		return new SelectionCriteria(columns);
	}

	public int size() {
		if (m_columns == null) {
			return 0;
		}

		return m_columns.size();
	}

	public String getColumn(int index) {
		return m_columns.get(index);
	}

	@Override
	public String toString() {
		if (m_columns == null || m_columns.isEmpty()) {
			return EMPTY_STRING;
		}

		StringBuilder sb = new StringBuilder();

		String prefix = EMPTY_STRING;
		for (int idx = 0; idx < size(); ++idx) {
			sb.append(prefix);
			prefix = DELIMITER;

			sb.append(getColumn(idx));
		}

		return sb.toString();
	}

	public static Builder newBuilder() {
		return new Builder(null);
	}

	public static Builder newBuilder(FilterCriteria filterCriteria) {
		return new Builder(filterCriteria);
	}

	public static class Builder {

		private FilterCriteria m_filterCriteria;
		private List<String> m_columns = new ArrayList<String>();

		private Builder(FilterCriteria filterCriteria) {
			m_filterCriteria = filterCriteria;
		}

		public Builder select(String column) {
			if (!isFiltered(column)) {
				m_columns.add(column);
			}

			return this;
		}

		public SelectionCriteria build() {
			return new SelectionCriteria(m_columns);
		}

		private boolean isFiltered(String column) {
			if (m_filterCriteria == null) {
				return false;
			}

			for (int idx = 0; idx < m_filterCriteria.size(); ++idx) {
				if (m_filterCriteria.getColumn(idx).equals(column)
						&& m_filterCriteria.getOperator(idx).equals(FilterCriteria.EQ)) {
					return true;
				}

			}

			return false;
		}
	}
}
