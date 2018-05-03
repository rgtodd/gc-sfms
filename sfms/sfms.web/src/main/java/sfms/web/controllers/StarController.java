package sfms.web.controllers;

import java.net.URI;
import java.util.List;
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
import sfms.web.models.StarSortingModel;
import sfms.web.schemas.StarModelSchema;

@Controller
public class StarController extends SfmsController {

	private final Logger logger = Logger.getLogger(StarController.class.getName());

	@GetMapping({ "/star/{id}" })
	public String get(@PathVariable String id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + id), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Star>() {
				});

		ModelFactory factory = new ModelFactory();
		StarModel starModel = factory.createStar(restResponse.getBody());

		modelMap.addAttribute("star", starModel);

		return "starDetail";
	}

	@GetMapping({ "/star" })
	public String getList(@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(WebParameters.SORT) Optional<String> sort,
			@RequestParam(WebParameters.DIRECTION) Optional<String> direction, ModelMap modelMap) {

		String effectiveSort;
		if (sort.isPresent()) {
			switch (sort.get()) {
			case StarModelSchema.X:
			case StarModelSchema.Y:
			case StarModelSchema.Z:
				effectiveSort = sort.get();
				break;
			default:
				effectiveSort = StarModelSchema.X;
				break;
			}
		} else {
			effectiveSort = StarModelSchema.X;
		}

		String effectiveDirection;
		if (direction.isPresent()) {
			switch (direction.get()) {
			case SortingModel.ASCENDING:
			case SortingModel.DESCENDING:
				effectiveDirection = direction.get();
				break;
			default:
				effectiveDirection = SortingModel.ASCENDING;
			}
		} else {
			effectiveDirection = SortingModel.ASCENDING;
		}

		StarField sortColumn = null;
		switch (effectiveSort) {
		case StarModelSchema.X:
			sortColumn = StarField.X;
			break;
		case StarModelSchema.Y:
			sortColumn = StarField.Y;
			break;
		case StarModelSchema.Z:
			sortColumn = StarField.Z;
			break;
		}

		SortCriteria sortCriteria;
		if (effectiveDirection.equals(SortingModel.ASCENDING)) {
			sortCriteria = SortCriteria.newBuilder().ascending(sortColumn.getName()).build();
		} else {
			sortCriteria = SortCriteria.newBuilder().descending(sortColumn.getName()).build();
		}

		UriBuilder uriBuilder = getUriBuilder().pathSegment("star");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
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

		StarSortingModel sortingModel = new StarSortingModel();
		sortingModel.setSort(effectiveSort);
		sortingModel.setDirection(effectiveDirection);
		modelMap.addAttribute("sorting", sortingModel);

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
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("star"), HttpMethod.PUT,
				createHttpEntity(star), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/star/" + restResponse.getBody().getKey();
	}

	@GetMapping({ "/star_edit/{id}" })
	public String edit(@PathVariable String id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + id), HttpMethod.GET,
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
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(getRestUrl("star/" + star.getKey()),
				HttpMethod.PUT, createHttpEntity(star), new ParameterizedTypeReference<UpdateResult<String>>() {
				});

		return "redirect:/star/" + restResponse.getBody().getKey();
	}

	@GetMapping({ "/star_delete/{id}" })
	public String delete(@PathVariable String id, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Star> restResponse = restTemplate.exchange(getRestUrl("star/" + id), HttpMethod.GET,
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
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(getRestUrl("star/" + star.getKey()),
				HttpMethod.DELETE, createHttpEntity(star), new ParameterizedTypeReference<DeleteResult<String>>() {
				});

		return "redirect:/star";
	}
}
