package sfms.storage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LocalStorageManager implements StorageManager {

	@Override
	public WritableByteChannel getWritableByteChannel(String bucketName, String blobName, String contentType)
			throws IOException {

		Path path = getPath(bucketName, blobName);

		WritableByteChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		return channel;
	}

	@Override
	public ReadableByteChannel getReadableByteChannel(String bucketName, String blobName) throws IOException {

		Path path = getPath(bucketName, blobName);

		ReadableByteChannel channel = FileChannel.open(path, StandardOpenOption.READ);

		return channel;
	}

	private Path getPath(String bucketName, String blobName) {

		Path path = FileSystems.getDefault().getPath(System.getProperty("user.home"), "Documents", bucketName,
				blobName);

		return path;
	}

}
