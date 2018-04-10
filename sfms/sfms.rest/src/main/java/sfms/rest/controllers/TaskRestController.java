package sfms.rest.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sfms.rest.db.business.StarImporter;

@RestController
@RequestMapping("/task")
public class TaskRestController {

	private final Logger logger = Logger.getLogger(TaskRestController.class.getName());

	@GetMapping(value = "/processStarFile")
	public void processStarFile(@RequestParam("filename") String filename) throws Exception {

		logger.log(Level.INFO, "Processing {0}.", filename);

		String bucketName = "rgt-ssms.appspot.com";
		String blobName = "uploads/" + filename;

		StarImporter importer = new StarImporter();
		importer.initialize();
		importer.process(bucketName, blobName);
	}
}
