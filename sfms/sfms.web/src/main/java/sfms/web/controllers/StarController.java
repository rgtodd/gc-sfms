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
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.StarField;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.PagingModel;
import sfms.web.models.SortingModel;
import sfms.web.models.StarModel;
import sfms.web.schemas.StarModelField;

@Controller
@RequestMapping({ "/star" })
public class StarController extends SfmsController {

	private final Logger logger = Logger.getLogger(StarController.class.getName());

	private static final Map<String, StarField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, StarField>();
		s_dbFieldMap.put(StarModelField.CATALOG_ID, StarField.CatalogId);
		s_dbFieldMap.put(StarModelField.PROPER_NAME, StarField.ProperName);
		s_dbFieldMap.put(StarModelField.SECTOR_KEY, StarField.SectorKey);
		s_dbFieldMap.put(StarModelField.CLUSTER_KEY, StarField.ClusterKey);
		s_dbFieldMap.put(StarModelField.X, StarField.X);
		s_dbFieldMap.put(StarModelField.Y, StarField.Y);
		s_dbFieldMap.put(StarModelField.Z, StarField.Z);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starDetail";
	}

	@GetMapping({ "" })
	public String getList(
			@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(name = WebParameters.SORT, defaultValue = StarModelField.CATALOG_ID) String sort,
			@RequestParam(name = WebParameters.DIRECTION, defaultValue = SortCriteria.ASCENDING) String direction,
			ModelMap modelMap) {

		URI uri = createListUri(sort, direction, bookmark);
		logger.log(Level.INFO, "uri = {0}", uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Star>> restResponse = restTemplate.exchange(uri, HttpMethod.GET, createHttpEntity(),
				new ParameterizedTypeReference<SearchResult<Star>>() {
				});

		SearchResult<Star> searchResult = restResponse.getBody();

		ModelFactory factory = new ModelFactory();
		List<StarModel> starModels = factory.createStars(searchResult.getEntities());
		modelMap.addAttribute("stars", starModels);

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
		modelMap.addAttribute("F", new StarModelField());

		return "starList";
	}

	@GetMapping({ "/create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar();

		modelMap.addAttribute("star", starModel);

		return "starCreate";
	}

	@PostMapping({ "/createPost" })
	public String createPost(@ModelAttribute StarModel starModel) {

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(starModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("star"), HttpMethod.PUT,
				createHttpEntity(star), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/star/" + restResponse.getBody().getKey();
	}

	@GetMapping({ "/edit/{key}" })
	public String edit(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starEdit";
	}

	@PostMapping({ "/editPost" })
	public String editPost(@ModelAttribute StarModel starModel) {

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(starModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(getRestUrl("star/" + star.getKey()),
				HttpMethod.PUT, createHttpEntity(star), new ParameterizedTypeReference<UpdateResult<String>>() {
				});

		return "redirect:/star/" + restResponse.getBody().getKey();
	}

	@GetMapping({ "/delete/{key}" })
	public String delete(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starDelete";
	}

	@PostMapping({ "/deletePost" })
	public String deletePost(@ModelAttribute StarModel starModel) {

		RestFactory factory = new RestFactory();
		Star star = factory.createStar(starModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(getRestUrl("star/" + star.getKey()),
				HttpMethod.DELETE, createHttpEntity(star), new ParameterizedTypeReference<DeleteResult<String>>() {
				});

		return "redirect:/star";
	}

	private URI createListUri(String sort, String direction, Optional<String> bookmark) {

		SortCriteria sortCriteria;
		if (sort.equals(StarModelField.XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(StarField.X.getName(), direction)
					.sort(StarField.Y.getName(), direction)
					.sort(StarField.Z.getName(), direction)
					.build();
		} else {
			StarField sortColumn = s_dbFieldMap.get(sort);
			sortCriteria = SortCriteria.newBuilder()
					.sort(sortColumn.getName(), direction)
					.build();
		}

		UriBuilder uriBuilder = getUriBuilder().pathSegment("star");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		return uri;
	}
}
