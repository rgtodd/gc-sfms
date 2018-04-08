package sfms.rest.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sfms.rest.db.business.StarClusterGenerator;

@RestController
@RequestMapping("/utility")
public class UtilityRestController {

	private final Logger logger = Logger.getLogger(UtilityRestController.class.getName());

	@GetMapping(value = "/generateClusters")
	public String generateClusters() {
		try {
			logger.info("Start - generateClusters.");

			StarClusterGenerator generator = new StarClusterGenerator();
			generator.generate();

			logger.info("End - generateClusters.");

			return "Complete.";
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "generateClusters exception occurred.", ex);
			return ex.getMessage();
		}
	}
}
