package sfms.rest.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sfms.common.Constants;
import sfms.rest.api.RestParameters;
import sfms.rest.db.business.StarImporter;

/**
 * Controller for the Task REST service.
 * 
 * These operations are the targets of App Engine task requests.
 * 
 */
@RestController
@RequestMapping("/task")
public class TaskRestController {

	private final Logger logger = Logger.getLogger(TaskRestController.class.getName());

	@GetMapping(value = "/processStarFile")
	public void processStarFile(@RequestParam(RestParameters.FILE_NAME) String filename,
			@RequestParam(RestParameters.START) Integer start, @RequestParam(RestParameters.COUNT) Integer count)
			throws Exception {

		logger.log(Level.INFO, "Processing {0}.", filename);

		String bucketName = Constants.CLOUD_STORAGE_BUCKET;
		String blobName = Constants.CLOUD_STOARGE_UPLOAD_FOLDER + "/" + filename;

		StarImporter importer = new StarImporter();
		importer.initialize();
		importer.process(bucketName, blobName, start, count);
	}
}
