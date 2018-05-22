package sfms.simulator.controllers;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sfms.simulator.Simulator;

/**
 * Controller for the Simulation REST service.
 * 
 * These operations simulate spaceship, crew member and other actor activity.
 */
@RestController
@RequestMapping("/simulation")
public class SimulationRestController {

	private final Logger logger = Logger.getLogger(SimulationRestController.class.getName());

	@PostMapping(value = "/simulate")
	public String simulate() {
		try {
			logger.info("Start - Simulate.");

			Instant now = Instant.now();

			Simulator simulator = new Simulator();
			simulator.processActors(now);

			logger.info("End - Simulate.");

			return "Complete.";
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "simulate exception occurred.", ex);
			return ex.getMessage();
		}
	}
}
