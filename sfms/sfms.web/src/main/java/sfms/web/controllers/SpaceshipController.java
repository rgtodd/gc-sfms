package sfms.web.controllers;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.SearchResult;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Spaceship;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.SpaceshipModel;

@Controller
public class SpaceshipController extends SfmsController {

	@GetMapping({ "/spaceship/{id}" })
	public String get(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(getRestUrl("spaceship/" + id.toString()),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipDetail";
	}

	@GetMapping({ "/spaceship" })
	public String getList(ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Spaceship>> restResponse = restTemplate.exchange(getRestUrl("spaceship"),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<SearchResult<Spaceship>>() {
				});

		ModelFactory factory = new ModelFactory();
		List<SpaceshipModel> spaceshipModels = factory.createSpaceships(restResponse.getBody().getEntities());

		modelMap.addAttribute("spaceships", spaceshipModels);

		return "spaceshipList";
	}

	@GetMapping({ "/spaceship_create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship();

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipCreate";
	}

	@PostMapping({ "/spaceship_createPost" })
	public String createPost(@ModelAttribute SpaceshipModel spaceshipModel) {

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(spaceshipModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("spaceship"),
				HttpMethod.PUT,
				createHttpEntity(spaceship), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/spaceship/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/spaceship_edit/{id}" })
	public String edit(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(getRestUrl("spaceship/" + id.toString()),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Spaceship>() {
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

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("spaceship/" + spaceship.getKey().toString()), HttpMethod.PUT, createHttpEntity(spaceship),
				new ParameterizedTypeReference<UpdateResult<String>>() {
				}

		);

		return "redirect:/spaceship/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/spaceship_delete/{id}" })
	public String delete(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(getRestUrl("spaceship/" + id.toString()),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Spaceship>() {
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

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("spaceship/" + spaceship.getKey().toString()), HttpMethod.DELETE,
				createHttpEntity(spaceship),
				new ParameterizedTypeReference<DeleteResult<String>>() {
				}

		);

		return "redirect:/spaceship";
	}
}
