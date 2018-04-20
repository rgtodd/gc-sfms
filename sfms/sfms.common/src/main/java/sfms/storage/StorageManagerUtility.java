package sfms.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.stream.Stream;

import sfms.common.Constants;

public class StorageManagerUtility {

	private final static String CACHE_BUCKET_NAME = Constants.CLOUD_STORAGE_BUCKET;
	private final static String CACHE_FOLDER_NAME = Constants.CLOUD_STOARGE_CACHE_FOLDER;

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

	public static int getLineCount(String bucketName, String blobName) throws IOException {

		try (ReadableByteChannel readChannel = Storage.getManager().getReadableByteChannel(bucketName, blobName)) {
			return getLineCount(readChannel);
		}
	}

	public static int getLineCount(ReadableByteChannel readChannel) throws IOException {

		int count = 0;
		try (InputStream inputStream = Channels.newInputStream(readChannel);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				Stream<String> lineStream = bufferedReader.lines()) {

			Iterator<String> iterator = lineStream.iterator();
			while (iterator.hasNext()) {
				count += 1;
				iterator.next();
			}

			return count;
		}
	}

}
