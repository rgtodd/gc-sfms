package sfms.web.controllers;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import sfms.rest.DeleteResult;
import sfms.rest.SearchResult;
import sfms.rest.UpdateResult;
import sfms.rest.models.Spaceship;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsProperties;
import sfms.web.models.SpaceshipModel;

@Controller
public class SpaceshipController {

	@GetMapping({ "/spaceship/{id}" })
	public String get(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(
				getSpaceshipRestUrl("spaceship/" + id.toString()), HttpMethod.GET, null,
				new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipDetail";
	}

	@GetMapping({ "/spaceship" })
	public String getList(ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<SearchResult<Spaceship>> restResponse = restTemplate.exchange(getSpaceshipRestUrl("spaceship"),
				HttpMethod.GET, null, new ParameterizedTypeReference<SearchResult<Spaceship>>() {
				});

		ModelFactory factory = new ModelFactory();
		List<SpaceshipModel> spaceshipModels = factory.createSpaceships(restResponse.getBody().getEntities());

		modelMap.addAttribute("spaceships", spaceshipModels);

		return "spaceshipList";
	}

	@GetMapping({ "/spaceship_edit/{id}" })
	public String edit(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(
				getSpaceshipRestUrl("spaceship/" + id.toString()), HttpMethod.GET, null,
				new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipEdit";
	}

	@PostMapping({ "/spaceship_editPost" })
	public String editPost(@ModelAttribute SpaceshipModel spaceshipModel) {

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(spaceshipModel);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<UpdateResult<Long>> restResponse = restTemplate.exchange(
				getSpaceshipRestUrl("spaceship/" + spaceship.getId().toString()), HttpMethod.POST,
				new HttpEntity<>(spaceship), new ParameterizedTypeReference<UpdateResult<Long>>() {
				}

		);

		return "redirect:/spaceship/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/spaceship_delete/{id}" })
	public String delete(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(
				getSpaceshipRestUrl("spaceship/" + id.toString()), HttpMethod.GET, null,
				new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipDelete";
	}

	@PostMapping({ "/spaceship_deletePost" })
	public String deletePost(@ModelAttribute SpaceshipModel spaceshipModel) {

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(spaceshipModel);

		RestTemplate restTemplate = new RestTemplate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<Long>> restResponse = restTemplate.exchange(
				getSpaceshipRestUrl("spaceship/" + spaceship.getId().toString()), HttpMethod.DELETE,
				new HttpEntity<>(spaceship), new ParameterizedTypeReference<DeleteResult<Long>>() {
				}

		);

		return "redirect:/spaceship";
	}

	private String getSpaceshipRestUrl(String url) {
		String host = SfmsProperties.INSTANCE.getProperty(SfmsProperties.APPLICATION, SfmsProperties.SFMS_REST_HOST);
		return host + "/" + url;
	}
}
