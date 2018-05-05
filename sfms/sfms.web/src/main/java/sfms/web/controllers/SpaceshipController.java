package sfms.web.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;

import sfms.rest.api.CreateResult;
import sfms.rest.api.DeleteResult;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SortCriteria;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Spaceship;
import sfms.rest.api.schemas.SpaceshipField;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.PagingModel;
import sfms.web.models.SortingModel;
import sfms.web.models.SpaceshipModel;
import sfms.web.schemas.SpaceshipModelField;

@Controller
@RequestMapping({ "/spaceship" })
public class SpaceshipController extends SfmsController {

	private final Logger logger = Logger.getLogger(SpaceshipController.class.getName());

	private static final Map<String, SpaceshipField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, SpaceshipField>();
		s_dbFieldMap.put(SpaceshipModelField.NAME, SpaceshipField.Name);
		s_dbFieldMap.put(SpaceshipModelField.X, SpaceshipField.X);
		s_dbFieldMap.put(SpaceshipModelField.Y, SpaceshipField.Y);
		s_dbFieldMap.put(SpaceshipModelField.Z, SpaceshipField.Z);
		s_dbFieldMap.put(SpaceshipModelField.STAR_KEY, SpaceshipField.StarKey);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable Long key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(getRestUrl("spaceship/" + key.toString()),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipDetail";
	}

	@GetMapping({ "" })
	public String getList(
			@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(name = WebParameters.SORT, defaultValue = SpaceshipModelField.NAME) String sort,
			@RequestParam(name = WebParameters.DIRECTION, defaultValue = SortCriteria.ASCENDING) String direction,
			ModelMap modelMap) {

		URI uri = createListUri(sort, direction, bookmark);
		logger.log(Level.INFO, "uri = {0}", uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Spaceship>> restResponse = restTemplate.exchange(uri, HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Spaceship>>() {
				});

		SearchResult<Spaceship> searchResult = restResponse.getBody();

		ModelFactory factory = new ModelFactory();
		List<SpaceshipModel> clusterModels = factory.createSpaceships(searchResult.getEntities());
		modelMap.addAttribute("spaceships", clusterModels);

		PagingModel pagingModel = new PagingModel();
		pagingModel.setNextBookmark(searchResult.getEndingBookmark());
		pagingModel.setEndOfResults(searchResult.getEndOfResults());
		pagingModel.setCurrentPageNumber(pageNumber.orElse(1));
		pagingModel.setNextPageNumber(pageNumber.orElse(1) + 1);
		modelMap.addAttribute("paging", pagingModel);

		SortingModel sortingModel = new SortingModel();
		sortingModel.setSort(sort);
		sortingModel.setDirection(direction);
		modelMap.addAttribute("sorting", sortingModel);
		modelMap.addAttribute("F", new SpaceshipModelField());

		return "spaceshipList";
	}

	@GetMapping({ "/create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship();

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipCreate";
	}

	@PostMapping({ "/createPost" })
	public String createPost(@ModelAttribute SpaceshipModel spaceshipModel) {

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(spaceshipModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("spaceship"),
				HttpMethod.POST, createHttpEntity(spaceship), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/spaceship/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/edit/{key}" })
	public String edit(@PathVariable Long key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(getRestUrl("spaceship/" + key.toString()),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipEdit";
	}

	@PostMapping({ "/editPost" })
	public String editPost(@ModelAttribute SpaceshipModel spaceshipModel) {

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(spaceshipModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("spaceship/" + spaceship.getKey().toString()), HttpMethod.PUT, createHttpEntity(spaceship),
				new ParameterizedTypeReference<UpdateResult<String>>() {
				});

		return "redirect:/spaceship/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/delete/{key}" })
	public String delete(@PathVariable Long key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Spaceship> restResponse = restTemplate.exchange(getRestUrl("spaceship/" + key.toString()),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<Spaceship>() {
				});

		ModelFactory factory = new ModelFactory();
		SpaceshipModel spaceshipModel = factory.createSpaceship(restResponse.getBody());

		modelMap.addAttribute("spaceship", spaceshipModel);

		return "spaceshipDelete";
	}

	@PostMapping({ "/deletePost" })
	public String deletePost(@ModelAttribute SpaceshipModel spaceshipModel) {

		RestFactory factory = new RestFactory();
		Spaceship spaceship = factory.createSpaceship(spaceshipModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("spaceship/" + spaceship.getKey().toString()), HttpMethod.DELETE,
				createHttpEntity(spaceship), new ParameterizedTypeReference<DeleteResult<String>>() {
				});

		return "redirect:/spaceship";
	}

	private URI createListUri(String sort, String direction, Optional<String> bookmark) {

		SpaceshipField sortColumn = s_dbFieldMap.get(sort);
		SortCriteria sortCriteria = SortCriteria.newBuilder()
				.sort(sortColumn.getName(), direction)
				.build();

		UriBuilder uriBuilder = getUriBuilder().pathSegment("spaceship");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		return uri;
	}
}
