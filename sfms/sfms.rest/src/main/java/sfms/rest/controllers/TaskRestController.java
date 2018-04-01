package sfms.rest.controllers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@RestController
@RequestMapping("/task")
public class TaskRestController {

	private final Logger logger = Logger.getLogger(TaskRestController.class.getName());

	@GetMapping(value = "/processStarFile")
	public void get(@RequestParam("filename") String filename) throws Exception {

		logger.log(Level.INFO, "Processing {0}.", filename);

		String bucketName = "rgt-ssms.appspot.com";
		String blobName = "uploads/" + filename;
		BlobId blobId = BlobId.of(bucketName, blobName);

		Storage storage = StorageOptions.getDefaultInstance().getService();
		try (ReadChannel readChannel = storage.reader(blobId)) {
			try (InputStream inputStream = Channels.newInputStream(readChannel)) {
				try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
					byte[] buffer = new byte[1024];
					int limit;
					int max = 5000;
					while ((limit = inputStream.read(buffer)) >= 0 && max > 0) {
						max -= limit;
						outputStream.write(buffer, 0, limit);
					}

					String head = outputStream.toString("UTF-8");
					logger.log(Level.INFO, "Head of file = {0}.", head);
				}
			}
		}

	}
}
