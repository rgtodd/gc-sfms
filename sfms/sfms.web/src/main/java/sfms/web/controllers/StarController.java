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

import sfms.rest.CreateResult;
import sfms.rest.DeleteResult;
import sfms.rest.SearchResult;
import sfms.rest.UpdateResult;
import sfms.rest.models.Star;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.StarModel;

@Controller
public class StarController extends SfmsController {

	@GetMapping({ "/star/{id}" })
	public String get(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + id.toString()), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starDetail";
	}

	@GetMapping({ "/star" })
	public String getList(ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Star>> restResponse = restTemplate.exchange(getRestUrl("star"), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Star>>() {
				});

		ModelFactory factory = new ModelFactory();
		List<StarModel> starModels = factory.createStars(restResponse.getBody().getEntities());

		modelMap.addAttribute("stars", starModels);

		return "starList";
	}

	@GetMapping({ "/star_create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar();

		modelMap.addAttribute("star", starModel);

		return "starCreate";
	}

	@PostMapping({ "/star_createPost" })
	public String createPost(@ModelAttribute StarModel starModel) {

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(starModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<Long>> restResponse = restTemplate.exchange(getRestUrl("star"), HttpMethod.PUT,
				createHttpEntity(star), new ParameterizedTypeReference<CreateResult<Long>>() {
				});

		return "redirect:/star/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/star_edit/{id}" })
	public String edit(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + id.toString()), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starEdit";
	}

	@PostMapping({ "/star_editPost" })
	public String editPost(@ModelAttribute StarModel starModel) {

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(starModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<Long>> restResponse = restTemplate.exchange(
				getRestUrl("star/" + star.getKey()), HttpMethod.PUT, createHttpEntity(star),
				new ParameterizedTypeReference<UpdateResult<Long>>() {
				}

		);

		return "redirect:/star/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/star_delete/{id}" })
	public String delete(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + id.toString()), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starDelete";
	}

	@PostMapping({ "/star_deletePost" })
	public String deletePost(@ModelAttribute StarModel starModel) {

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(starModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<Long>> restResponse = restTemplate.exchange(
				getRestUrl("star/" + star.getKey()), HttpMethod.DELETE, createHttpEntity(star),
				new ParameterizedTypeReference<DeleteResult<Long>>() {
				}

		);

		return "redirect:/star";
	}
}
