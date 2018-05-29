package sfms.db;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CompositeKeyBuilder {

	private static final String[] PROTOTYPE = new String[0];

	private List<String> m_fields;

	private CompositeKeyBuilder() {

	}

	public static CompositeKeyBuilder create() {
		return new CompositeKeyBuilder();
	}

	public static long getSerialNumber(Instant instant) {
		return Long.MAX_VALUE - instant.getEpochSecond();
	}

	public CompositeKeyBuilder append(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Argument value is null.");
		}

		appendValue(value);

		return this;
	}

	public CompositeKeyBuilder append(String value, int length) {
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

	public CompositeKeyBuilder append(long value, int length) {
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

	public CompositeKeyBuilder append(int value) {
		append(value, 10);

		return this;
	}

	public CompositeKeyBuilder append(long value) {
		append(value, 19);

		return this;
	}

	public CompositeKeyBuilder appendSeconds(Instant value) {
		append(value.getEpochSecond());

		return this;
	}

	public CompositeKeyBuilder appendDescendingSeconds(Instant value) {
		append(Long.MAX_VALUE - value.getEpochSecond());

		return this;
	}

	public CompositeKeyBuilder appendSerialNumber(Instant value) {
		append(getSerialNumber(value));

		return this;
	}

	public CompositeKey build() {
		if (m_fields == null) {
			return null;
		}
		return new CompositeKey(m_fields.toArray(PROTOTYPE));
	}

	@Override
	public String toString() {
		return build().toString();
	}

	private void appendValue(String value) {
		if (m_fields == null) {
			m_fields = new ArrayList<String>();
		}
		m_fields.add(value);
	}

}
