package sfms.web.models;

import java.text.DecimalFormat;

public class ModelUtility {

	private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("#.00");

	private ModelUtility() {

	}

	public static String formatCoordinates(Long x, Long y, Long z) {
		StringBuilder sb = new StringBuilder();

		sb.append('(');
		if (x == null) {
			sb.append('-');
		} else {
			sb.append(x);
		}
		sb.append(',');
		if (y == null) {
			sb.append('-');
		} else {
			sb.append(y);
		}
		sb.append(',');
		if (z == null) {
			sb.append('-');
		} else {
			sb.append(z);
		}
		sb.append(')');

		return sb.toString();
	}

	public static String formatCoordinates(Double x, Double y, Double z) {
		StringBuilder sb = new StringBuilder();

		sb.append('(');
		if (x == null) {
			sb.append('-');
		} else {
			sb.append(DOUBLE_FORMAT.format(x));
		}
		sb.append(',');
		if (y == null) {
			sb.append('-');
		} else {
			sb.append(DOUBLE_FORMAT.format(y));
		}
		sb.append(',');
		if (z == null) {
			sb.append('-');
		} else {
			sb.append(DOUBLE_FORMAT.format(z));
		}
		sb.append(')');

		return sb.toString();
	}

	public static String formatKey(String name, String value) {
		StringBuilder sb = new StringBuilder();

		if (name == null) {
			sb.append('-');
		} else {
			sb.append(name);
		}
		sb.append('/');
		if (value == null) {
			sb.append('-');
		} else {
			sb.append(value);
		}

		return sb.toString();
	}

}
