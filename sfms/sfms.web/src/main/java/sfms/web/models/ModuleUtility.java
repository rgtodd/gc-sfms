package sfms.web.models;

public class ModuleUtility {

	private ModuleUtility() {

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

}
