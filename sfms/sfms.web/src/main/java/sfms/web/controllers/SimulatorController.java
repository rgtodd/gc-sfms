package sfms.web.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Logger;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import sfms.simulator.api.models.SimulatorOptions;
import sfms.simulator.api.models.SimulatorStatus;
import sfms.simulator.api.schemas.WorkerStatus;
import sfms.web.SfmsController;
import sfms.web.models.SimulatorOptionsModel;
import sfms.web.models.SimulatorStatusModel;

@Controller
@RequestMapping({ "/simulator" })
public class SimulatorController extends SfmsController {

	private final Logger logger = Logger.getLogger(SimulatorController.class.getName());

	@GetMapping({ "" })
	public String simulation(ModelMap modelMap) {

		String url = getSimulatorUrl("admin/status");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SimulatorStatus> response = restTemplate.exchange(url, HttpMethod.GET, createHttpEntity(),
				new ParameterizedTypeReference<SimulatorStatus>() {
				});

		SimulatorStatus status = response.getBody();

		SimulatorStatusModel statusModel = new SimulatorStatusModel();
		statusModel.setJobWorkerStatus(status.getJobWorkerStatus());
		statusModel.setCanStartJobWorker(status.getJobWorkerStatus().equals(WorkerStatus.INACTIVE));
		statusModel.setCanStopJobWorker(status.getJobWorkerStatus().equals(WorkerStatus.ACTIVE));
		statusModel.setTransactionWorkerStatus(status.getTransactionWorkerStatus());
		statusModel.setCanStartTransactionWorker(status.getTransactionWorkerStatus().equals(WorkerStatus.INACTIVE));
		statusModel.setCanStopTransactionWorker(status.getTransactionWorkerStatus().equals(WorkerStatus.ACTIVE));

		SimulatorOptionsModel optionsModel = new SimulatorOptionsModel();
		optionsModel.setReset(false);
		optionsModel.setNow(LocalDateTime.now());

		modelMap.addAttribute("status", statusModel);
		modelMap.addAttribute("options", optionsModel);

		return "simulator";
	}

	@GetMapping({ "startJobWorker" })
	public String startJobWorker() {

		String url = getSimulatorUrl("admin/worker/control/status");
		String status = WorkerStatus.ACTIVE;

		updateWorkerStatus(url, status);

		return "redirect:/simulator/";
	}

	@GetMapping({ "stopJobWorker" })
	public String stopJobWorker() {

		String url = getSimulatorUrl("admin/worker/control/status");
		String status = WorkerStatus.INACTIVE;

		updateWorkerStatus(url, status);

		return "redirect:/simulator/";
	}

	@GetMapping({ "startTransactionWorker" })
	public String startTransactionWorker() {

		String url = getSimulatorUrl("admin/worker/transaction/status");
		String status = WorkerStatus.ACTIVE;

		updateWorkerStatus(url, status);

		return "redirect:/simulator/";
	}

	@GetMapping({ "stopTransactionWorker" })
	public String stopSimulatorWorker() {

		String url = getSimulatorUrl("admin/worker/transaction/status");
		String status = WorkerStatus.INACTIVE;

		updateWorkerStatus(url, status);

		return "redirect:/simulator/";
	}

	@GetMapping({ "initializeActors" })
	public String initializeActors() {

		String url = getSimulatorUrl("simulation/initializeActors");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(),
				Object.class);

		return "redirect:/simulator/";
	}

	@GetMapping("createMissions")
	public String createMissions() {

		String url = getSimulatorUrl("simulation/createMissions");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(),
				Object.class);

		return "redirect:/simulator/";
	}

	@PostMapping(value = "runPost", params = "action=initialize")
	public String runPostInitialize(@ModelAttribute SimulatorOptionsModel optionsModel) {

		SimulatorOptions options = new SimulatorOptions();
		if (optionsModel != null) {
			options.setNow(Instant.ofEpochSecond(optionsModel.getNow().toEpochSecond(ZoneOffset.UTC)));
			options.setReset(optionsModel.getReset());
		}

		String url = getSimulatorUrl("simulation/initializeActors");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(options),
				Object.class);

		return "redirect:/simulator/";
	}

	@PostMapping(value = "runPost", params = "action=update")
	public String runPostUpdate(@ModelAttribute SimulatorOptionsModel optionsModel) {

		SimulatorOptions options = new SimulatorOptions();
		if (optionsModel != null) {
			options.setNow(Instant.ofEpochSecond(optionsModel.getNow().toEpochSecond(ZoneOffset.UTC)));
			options.setReset(optionsModel.getReset());
		}

		String url = getSimulatorUrl("simulation/updateActors");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(options),
				Object.class);

		return "redirect:/simulator/";
	}

	private void updateWorkerStatus(String url, String status) {
		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(status),
				new ParameterizedTypeReference<String>() {
				});

		String newStatus = response.getBody();

		logger.info("Response = " + newStatus);
	}

}
