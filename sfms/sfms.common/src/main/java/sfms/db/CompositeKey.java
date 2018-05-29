package sfms.db;

import java.time.Instant;

public class CompositeKey {

	private static final String DELIMITER = "-";
	private static final String DELIMITER_REGEX = "-";

	private String[] m_fields;

	CompositeKey(String[] fields) {
		if (fields == null) {
			throw new IllegalArgumentException("Argument fields is null.");
		}

		m_fields = fields;
	}

	public static CompositeKey parse(String value) {
		String[] values = value.split(DELIMITER_REGEX);
		return new CompositeKey(values);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		String prefix = "";
		for (String field : m_fields) {
			sb.append(prefix);
			prefix = DELIMITER;

			sb.append(field);
		}

		return sb.toString();
	}

	public String getField(int index) {
		return m_fields[index];
	}

	public long getLong(int index) {
		return Long.parseLong(getField(index));
	}

	public Instant getFromSeconds(int index) {
		long seconds = getLong(index);
		return Instant.ofEpochSecond(seconds);
	}

	public Instant getFromSecondsDescending(int index) {
		long seconds = Long.MAX_VALUE - getLong(index);
		return Instant.ofEpochSecond(seconds);
	}

}
