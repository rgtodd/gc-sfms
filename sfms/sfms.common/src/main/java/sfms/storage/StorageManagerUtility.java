package sfms.storage;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class StorageManagerUtility {

	private final static String CACHE_BUCKET_NAME = "rgt-ssms.appspot.com";
	private final static String CACHE_FOLDER_NAME = "cache";

	private StorageManagerUtility() {

	}

	public static ReadableByteChannel getCachedObject(StorageManager storageManager, String objectName,
			String contentType,
			ObjectFactory factory) throws Exception {

		String blobName = CACHE_FOLDER_NAME + "/" + objectName.replace(',', '-');

		if (!storageManager.blobExists(CACHE_BUCKET_NAME, blobName)) {
			byte[] buffer = factory.createObject();
			try (WritableByteChannel writeChannel = Storage.getManager().getWritableByteChannel(CACHE_BUCKET_NAME,
					blobName, contentType)) {
				writeChannel.write(ByteBuffer.wrap(buffer));
			}
		}

		ReadableByteChannel readChannel = Storage.getManager().getReadableByteChannel(CACHE_BUCKET_NAME,
				blobName);

		return readChannel;
	}

	public interface ObjectFactory {

		public byte[] createObject() throws Exception;

	}

}
