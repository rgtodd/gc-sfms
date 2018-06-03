package sfms.db;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CompositeKeyBuilder {

	private static final String[] PROTOTYPE = new String[0];

	public static final int DEFAULT_INTEGER_DIGITS = 10;
	public static final int DEFAULT_LONG_DIGITS = 19;

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
		return append(value, DEFAULT_INTEGER_DIGITS);
	}

	public CompositeKeyBuilder append(long value) {
		return append(value, DEFAULT_LONG_DIGITS);
	}

	public CompositeKeyBuilder appendSeconds(Instant value) {
		return append(value.getEpochSecond());
	}

	public CompositeKeyBuilder appendDescendingSeconds(Instant value) {
		return append(Long.MAX_VALUE - value.getEpochSecond());
	}

	public CompositeKeyBuilder appendSerialNumber(Instant value) {
		return append(getSerialNumber(value));
	}

	public CompositeKeyBuilder appendHash2(int value) {
		return append(getHash2(value));
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

	private String getHash2(int value) {
		value = generateHash(value) % 100;
		return String.format("%02d", value);
	}

	private void appendValue(String value) {
		if (m_fields == null) {
			m_fields = new ArrayList<String>();
		}
		m_fields.add(value);
	}

	private int generateHash(int key) {
		key = ~key + (key << 15); // key = (key << 15) - key - 1;
		key = key ^ (key >>> 12);
		key = key + (key << 2);
		key = key ^ (key >>> 4);
		key = key * 2057; // key = (key + (key << 3)) + (key << 11);
		key = key ^ (key >>> 16);
		return Math.abs(key);
	}

}
