package sfms.web.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
import sfms.simulator.api.models.WorkerStatus;
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
		WorkerStatus controlStatus = status.getControlWorkerStatus();
		WorkerStatus transactionStatus = status.getTransactionWorkerStatus();

		SimulatorStatusModel statusModel = new SimulatorStatusModel();

		statusModel.setControlWorkerStatus(controlStatus.getStatus());
		statusModel.setControlWorkerRequestCount(controlStatus.getRequestCount());
		statusModel.setCanStartControlWorker(controlStatus.getStatus().equals(WorkerStatus.INACTIVE));
		statusModel.setCanStopControlWorker(controlStatus.getStatus().equals(WorkerStatus.ACTIVE));

		statusModel.setTransactionWorkerStatus(transactionStatus.getStatus());
		statusModel.setTransactionWorkerRequestCount(transactionStatus.getRequestCount());
		statusModel.setCanStartTransactionWorker(transactionStatus.getStatus().equals(WorkerStatus.INACTIVE));
		statusModel.setCanStopTransactionWorker(transactionStatus.getStatus().equals(WorkerStatus.ACTIVE));

		LocalDateTime now;
		if (status.getSimulationInstant() != null) {
			Instant nextInstant = status.getSimulationInstant().plus(1, ChronoUnit.DAYS);
			now = LocalDateTime.ofInstant(nextInstant, ZoneOffset.UTC);
		} else {
			now = LocalDateTime.now();
		}

		SimulatorOptionsModel optionsModel = new SimulatorOptionsModel();
		optionsModel.setReset(false);
		optionsModel.setNow(now);

		modelMap.addAttribute("status", statusModel);
		modelMap.addAttribute("options", optionsModel);

		return "simulator";
	}

	@GetMapping({ "startControlWorker" })
	public String startControlWorker() {

		String url = getSimulatorUrl("admin/worker/control/status");
		String status = WorkerStatus.ACTIVE;

		updateWorkerStatus(url, status);

		return "redirect:/simulator/";
	}

	@GetMapping({ "stopControlWorker" })
	public String stopControlWorker() {

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

	@GetMapping({ "resetSimulation" })
	public String resetSimulation() {

		String url = getSimulatorUrl("admin/resetSimulation");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(),
				Object.class);

		return "redirect:/simulator/";
	}

	@PostMapping(value = "runPost", params = "action=createMissions")
	public String runPostCreateMissions(@ModelAttribute SimulatorOptionsModel optionsModel) {

		SimulatorOptions options = createSimulatorOptions(optionsModel);

		String url = getSimulatorUrl("simulation/createMissions");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(options),
				Object.class);

		return "redirect:/simulator/";
	}

	@PostMapping(value = "runPost", params = "action=initialize")
	public String runPostInitialize(@ModelAttribute SimulatorOptionsModel optionsModel) {

		SimulatorOptions options = createSimulatorOptions(optionsModel);

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

		SimulatorOptions options = createSimulatorOptions(optionsModel);

		String url = getSimulatorUrl("simulation/updateActors");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		restTemplate.exchange(url, HttpMethod.POST,
				createHttpEntity(options),
				Object.class);

		return "redirect:/simulator/";
	}

	private SimulatorOptions createSimulatorOptions(SimulatorOptionsModel optionsModel) {
		SimulatorOptions options = new SimulatorOptions();
		if (optionsModel != null) {
			options.setNow(Instant.ofEpochSecond(optionsModel.getNow().toEpochSecond(ZoneOffset.UTC)));
			options.setCount(optionsModel.getCount());
			options.setReset(optionsModel.getReset());
		}
		return options;
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
