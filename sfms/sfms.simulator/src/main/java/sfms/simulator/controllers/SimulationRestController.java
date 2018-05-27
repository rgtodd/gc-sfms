package sfms.simulator.controllers;

import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sfms.simulator.MissionGenerator;
import sfms.simulator.worker.Worker;
import sfms.simulator.worker.functions.CreateMissions;
import sfms.simulator.worker.functions.InitializeActors;

/**
 * Controller for the Simulation REST service.
 * 
 * These operations simulate spaceship, crew member and other actor activity.
 */
@RestController
@RequestMapping("/simulation")
public class SimulationRestController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(SimulationRestController.class.getName());

	@Autowired
	private Worker controlWorker;

	@Autowired
	private Worker transactionWorker;

	@PostMapping(value = "/initializeActors")
	public void initializeActors() throws InterruptedException, TimeoutException {
		Instant now = Instant.now();
		boolean reset = true; // TODO: Pass reset via parameter.
		controlWorker.process(new InitializeActors(transactionWorker, now, reset));
	}

	@PostMapping(value = "/createMissions")
	public void createMissions() throws InterruptedException, TimeoutException {
		Instant now = Instant.now();
		MissionGenerator missionGenerator = new MissionGenerator();
		boolean reset = true; // TODO: Pass reset via parameter.
		controlWorker.process(new CreateMissions(transactionWorker, now, missionGenerator, reset));
	}
}
