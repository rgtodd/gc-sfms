package sfms.storage;

import sfms.common.PropertyFile;

/**
 * Exposes the appropriate {@link StorageManager} for the application.
 *
 */
public class Storage {

	private Storage() {
	}

	public static StorageManager getManager() {

		if (PropertyFile.INSTANCE.isProduction()) {
			return new GoogleStorageManager();
		}

		if (PropertyFile.INSTANCE.isDevelopment()) {
			return new LocalStorageManager();
		}

		return null;
	}
}
