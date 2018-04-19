package sfms.storage;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface StorageManager {

	public boolean blobExists(String bucketName, String blobName);

	public WritableByteChannel getWritableByteChannel(String bucketName, String blobName, String contentType)
			throws IOException;

	public ReadableByteChannel getReadableByteChannel(String bucketName, String blobName) throws IOException;

}
