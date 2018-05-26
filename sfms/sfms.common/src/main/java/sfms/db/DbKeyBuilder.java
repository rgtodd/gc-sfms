package sfms.db;

import java.time.Instant;

public class DbKeyBuilder {

	private static final String DELIMITER = "-";

	private StringBuilder m_sb;

	private DbKeyBuilder() {

	}

	public static DbKeyBuilder create() {
		return new DbKeyBuilder();
	}

	public DbKeyBuilder append(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Argument value is null.");
		}

		appendValue(value);

		return this;
	}

	public DbKeyBuilder append(String value, int length) {
		if (value == null) {
			throw new IllegalArgumentException("Argument value is null.");
		}
		if (length < 0 || length > 100) {
			throw new IllegalArgumentException("Argument length out of range.");
		}

		for (int idx = value.length(); idx < length; ++idx) {
			value = " " + value;
		}
		appendValue(value);

		return this;
	}

	public DbKeyBuilder append(long value, int length) {
		if (value < 0) {
			throw new IllegalArgumentException("Argument value is less than zero.");
		}
		if (length < 0 || length > 19) {
			throw new IllegalArgumentException("Argument length out of range.");
		}

		String format = "%0" + length + "d";
		String formattedValue = String.format(format, value);
		appendValue(formattedValue);

		return this;
	}

	public DbKeyBuilder append(int value) {
		append(value, 10);

		return this;
	}

	public DbKeyBuilder append(long value) {
		append(value, 19);

		return this;
	}

	public DbKeyBuilder appendSeconds(Instant value) {
		append(value.getEpochSecond());

		return this;
	}

	public DbKeyBuilder appendDescendingSeconds(Instant value) {
		append(Long.MAX_VALUE - value.getEpochSecond());

		return this;
	}

	public String build() {
		if (m_sb == null) {
			return "";
		} else {
			return m_sb.toString();
		}
	}

	@Override
	public String toString() {
		return build();
	}

	private void appendValue(String value) {
		if (m_sb == null) {
			m_sb = new StringBuilder();
		} else {
			m_sb.append(DELIMITER);
		}
		m_sb.append(value);
	}

}
