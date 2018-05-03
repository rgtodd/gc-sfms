package sfms.rest.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sfms.rest.api.RestParameters;
import sfms.rest.db.business.CrewMemberGenerator;
import sfms.rest.db.business.SpaceshipGenerator;
import sfms.rest.db.business.StarClusterGenerator;

/**
 * Controller for the Utility REST service.
 * 
 * These operations perform maintenance and other miscellaneous operations.
 *
 */
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

	@GetMapping(value = "/generateCrewMembers")
	public String generateCrewMembers(@RequestParam(RestParameters.COUNT) int count) {
		try {
			logger.info("Start - generateCrewMembers.");

			CrewMemberGenerator generator = new CrewMemberGenerator();
			generator.generate(count);

			logger.info("End - generateCrewMembers.");

			return "Complete.";
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "generateClusters exception occurred.", ex);
			return ex.getMessage();
		}
	}

	@GetMapping(value = "/generateSpaceships")
	public String generateSpaceships(@RequestParam(RestParameters.COUNT) int count) {
		try {
			logger.info("Start - generateSpaceships.");

			SpaceshipGenerator generator = new SpaceshipGenerator();
			generator.generate(count);

			logger.info("End - generateSpaceships.");

			return "Complete.";
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "generateClusters exception occurred.", ex);
			return ex.getMessage();
		}
	}
}
