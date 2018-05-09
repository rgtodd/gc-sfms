package sfms.common;

/**
 * Defines common constant values used by the REST and Web services.
 *
 */
public class Constants {

	public static final String CLOUD_STORAGE_BUCKET = "rgt-ssms.appspot.com";
	public static final String CLOUD_STOARGE_UPLOAD_FOLDER = "uploads";
	public static final String CLOUD_STOARGE_CACHE_FOLDER = "cache";

	public static final String CONTENT_TYPE_JSON = "application/json";

	public static final int SECTOR_MINIMUM_BOUNDS = -1000;
	public static final int SECTOR_MAXIMUM_BOUNDS = 1000;
	public static final int SECTOR_BOUNDS_DELTA = 200;

	public static final int BUFFER_SIZE = 1048576; // 1 Meg

	private Constants() {

	}
}
