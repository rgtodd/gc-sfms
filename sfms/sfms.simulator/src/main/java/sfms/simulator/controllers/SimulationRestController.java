package sfms.simulator.controllers;

import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import sfms.simulator.MissionGenerator;
import sfms.simulator.Simulation;
import sfms.simulator.api.models.SimulatorOptions;
import sfms.simulator.worker.Worker;
import sfms.simulator.worker.functions.CreateMissions;
import sfms.simulator.worker.functions.InitializeActors;
import sfms.simulator.worker.functions.UpdateActors;

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
	public void initializeActors(@RequestBody SimulatorOptions simulatorOptions)
			throws InterruptedException, TimeoutException {

		Instant now;
		if (simulatorOptions != null && simulatorOptions.getNow() != null) {
			now = simulatorOptions.getNow();
		} else {
			now = Instant.now();
		}

		boolean reset;
		if (simulatorOptions != null && simulatorOptions.getReset() != null) {
			reset = simulatorOptions.getReset();
		} else {
			reset = false;
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		controlWorker.process(new InitializeActors(datastore, transactionWorker, now, reset));
	}

	@PostMapping(value = "/updateActors")
	public void updateActors(@RequestBody SimulatorOptions simulatorOptions)
			throws InterruptedException, TimeoutException {

		Instant now;
		if (simulatorOptions != null && simulatorOptions.getNow() != null) {
			now = simulatorOptions.getNow();
		} else {
			now = Instant.now();
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

		Simulation simulation = new Simulation(now);
		simulation.setTimestamp(now);
		simulation.save(datastore);

		controlWorker.process(new UpdateActors(datastore, transactionWorker, now));
	}

	@PostMapping(value = "/createMissions")
	public void createMissions(@RequestBody SimulatorOptions simulatorOptions)
			throws InterruptedException, TimeoutException {

		Instant now;
		if (simulatorOptions != null && simulatorOptions.getNow() != null) {
			now = simulatorOptions.getNow();
		} else {
			now = Instant.now();
		}

		boolean reset;
		if (simulatorOptions != null && simulatorOptions.getReset() != null) {
			reset = simulatorOptions.getReset();
		} else {
			reset = false;
		}

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		MissionGenerator missionGenerator = new MissionGenerator();
		controlWorker.process(new CreateMissions(datastore, transactionWorker, now, missionGenerator, reset));
	}
}
