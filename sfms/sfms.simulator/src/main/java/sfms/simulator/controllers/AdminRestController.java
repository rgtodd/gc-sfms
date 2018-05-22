package sfms.simulator.controllers;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sfms.simulator.api.models.SimulatorStatus;

/**
 * Controller for the Admin REST service.
 * 
 * These operations administrate the simulator server.
 */
@RestController
@RequestMapping("/admin")
public class AdminRestController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(AdminRestController.class.getName());

	@GetMapping(value = "/status")
	public SimulatorStatus getStatus() {

		SimulatorStatus response = new SimulatorStatus();
		response.setStatus("OK");

		return response;
	}
}
