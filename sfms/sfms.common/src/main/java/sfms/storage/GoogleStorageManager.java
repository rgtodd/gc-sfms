package sfms.storage;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GoogleStorageManager implements StorageManager {

	@Override
	public WritableByteChannel getWritableByteChannel(String bucketName, String blobName, String contentType)
			throws IOException {

		BlobId blobId = BlobId.of(bucketName, blobName);

		BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
				.setContentType(contentType)
				.build();

		Storage storage = StorageOptions.getDefaultInstance().getService();

		WritableByteChannel channel = storage.writer(blobInfo);

		return channel;
	}

	@Override
	public ReadableByteChannel getReadableByteChannel(String bucketName, String blobName) {

		BlobId blobId = BlobId.of(bucketName, blobName);

		Storage storage = StorageOptions.getDefaultInstance().getService();

		ReadableByteChannel channel = storage.reader(blobId);

		return channel;
	}
}
