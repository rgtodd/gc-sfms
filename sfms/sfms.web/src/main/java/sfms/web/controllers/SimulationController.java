package sfms.web.controllers;

import java.util.logging.Logger;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import sfms.simulator.api.models.SimulatorStatus;
import sfms.web.SfmsController;

@Controller
@RequestMapping({ "/simulation" })
public class SimulationController extends SfmsController {

	private final Logger logger = Logger.getLogger(SimulationController.class.getName());

	@GetMapping({ "" })
	public String simulation(ModelMap modelMap) {

		String url = getSimulatorUrl("admin/status");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SimulatorStatus> response = restTemplate.exchange(url, HttpMethod.GET, createHttpEntity(),
				new ParameterizedTypeReference<SimulatorStatus>() {
				});

		modelMap.addAttribute("status", response.getBody());

		return "simulation";
	}

	@GetMapping({ "/simulate" })
	public String generateSpaceships(ModelMap modelMap) {

		String url = getRestUrl("simulation/simulate");

		logger.info("Calling " + url);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, createHttpEntity(),
				new ParameterizedTypeReference<String>() {
				});

		logger.info("Response = " + response);

		return "redirect:/simulation";
	}
}
