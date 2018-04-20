package sfms.rest.db;

public class CsvValue {

	private CsvValue() {

	}

	public static String getString(String[] values, int index) {
		return processString(getValue(values, index));
	}

	public static String getString(String[] values, int index, String defaultValue) {
		return processString(getValue(values, index), defaultValue);
	}

	public static double getDouble(String[] values, int index) {
		return processDouble(getValue(values, index));
	}

	public static long getLong(String[] values, int index) {
		return processLong(getValue(values, index));
	}

	public static Double getOptionalDouble(String[] values, int index) {
		return processOptionalDouble(getValue(values, index));
	}

	private static String getValue(String[] value, int index) {
		if (index < value.length) {
			return value[index];
		}
		return null;
	}

	private static String processString(String value) {
		return value;
	}

	private static String processString(String value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	private static double processDouble(String value) {
		return Double.parseDouble(value);
	}

	private static long processLong(String value) {
		return Long.parseLong(value);
	}

	private static Double processOptionalDouble(String value) {
		if (value == null || value.isEmpty())
			return null;
		return Double.parseDouble(value);
	}

}
