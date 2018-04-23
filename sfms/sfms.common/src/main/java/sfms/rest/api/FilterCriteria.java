package sfms.rest.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the filter criteria used by REST service search methods.
 * 
 * REST service callers can use {@link #newBuilder()} to construct a new
 * FilterCriteria object. Once the object has been created, call
 * {@link #toString()} to create the value to be passed to the service. The
 * resulting value should be URL encoded.
 * 
 * REST services can reconstruct the FilterCriteria object using the
 * {@link #parse(String)} function.
 *
 */
public class FilterCriteria {

	public static final String EQ = "eq";
	public static final String GT = "gt";
	public static final String GE = "ge";
	public static final String LT = "lt";
	public static final String LE = "le";

	private static final String EMPTY_STRING = "";
	private static final String RECORD_DELIMITER = ",";
	private static final String RECORD_DELIMITER_REGEX = RECORD_DELIMITER;
	private static final String FIELD_DELIMITER = " ";
	private static final String FIELD_DELIMITER_REGEX = "\\s";

	private List<String> m_columns;
	private List<String> m_operators;
	private List<String> m_values;

	private FilterCriteria(List<String> columns, List<String> operators, List<String> values) {
		m_columns = columns;
		m_operators = operators;
		m_values = values;
	}

	public static FilterCriteria parse(String text) {
		if (text == null)
			return null;

		String[] records = text.split(RECORD_DELIMITER_REGEX);

		List<String> columns = new ArrayList<String>(records.length);
		List<String> operators = new ArrayList<String>(records.length);
		List<String> values = new ArrayList<String>(records.length);

		for (int idx = 0; idx < records.length; ++idx) {
			String record = records[idx];

			String[] fields = record.split(FIELD_DELIMITER_REGEX);
			columns.add(fields[0]);
			operators.add(fields[1]);
			values.add(fields[2]);
		}

		return new FilterCriteria(columns, operators, values);
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

	public String getOperator(int index) {
		return m_operators.get(index);
	}

	public String getValue(int index) {
		return m_values.get(index);
	}

	public String toString() {
		if (m_columns == null || m_columns.isEmpty()) {
			return EMPTY_STRING;
		} else {
			StringBuilder sb = new StringBuilder();

			String prefix = EMPTY_STRING;
			for (int idx = 0; idx < size(); ++idx) {
				sb.append(prefix);
				prefix = RECORD_DELIMITER;

				sb.append(getColumn(idx));
				sb.append(FIELD_DELIMITER);
				sb.append(getOperator(idx));
				sb.append(FIELD_DELIMITER);
				sb.append(getValue(idx));
			}

			return sb.toString();
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private List<String> m_columns = new ArrayList<String>();
		private List<String> m_operators = new ArrayList<String>();
		private List<String> m_values = new ArrayList<String>();

		private Builder() {
		}

		public Builder add(String column, String operator, String value) {
			m_columns.add(column);
			m_operators.add(operator);
			m_values.add(value);

			return this;
		}

		public FilterCriteria build() {
			return new FilterCriteria(m_columns, m_operators, m_values);
		}
	}
}
