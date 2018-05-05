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
import sfms.rest.api.models.Sector;
import sfms.rest.api.schemas.SectorField;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.PagingModel;
import sfms.web.models.SectorModel;
import sfms.web.models.SortingModel;
import sfms.web.schemas.SectorModelField;

@Controller
@RequestMapping({ "/sector" })
public class SectorController extends SfmsController {

	private final Logger logger = Logger.getLogger(SectorController.class.getName());

	private static final Map<String, SectorField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, SectorField>();
		s_dbFieldMap.put(SectorModelField.SectorX, SectorField.SectorX);
		s_dbFieldMap.put(SectorModelField.SectorY, SectorField.SectorY);
		s_dbFieldMap.put(SectorModelField.SectorZ, SectorField.SectorZ);
		s_dbFieldMap.put(SectorModelField.MinimumX, SectorField.MinimumX);
		s_dbFieldMap.put(SectorModelField.MinimumY, SectorField.MinimumY);
		s_dbFieldMap.put(SectorModelField.MinimumZ, SectorField.MinimumZ);
		s_dbFieldMap.put(SectorModelField.MaximumX, SectorField.MaximumX);
		s_dbFieldMap.put(SectorModelField.MaximumY, SectorField.MaximumY);
		s_dbFieldMap.put(SectorModelField.MaximumZ, SectorField.MaximumZ);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Sector> restResponse = restTemplate.exchange(getRestUrl("sector/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Sector>() {
				});

		ModelFactory factory = new ModelFactory();
		SectorModel sectorModel = factory.createSector(restResponse.getBody());

		modelMap.addAttribute("sector", sectorModel);

		return "sectorDetail";
	}

	@GetMapping({ "" })
	public String getList(
			@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(WebParameters.SORT) Optional<String> sort,
			@RequestParam(WebParameters.DIRECTION) Optional<String> direction, ModelMap modelMap) {

		String effectiveSort;
		if (sort.isPresent()) {
			effectiveSort = sort.get();
		} else {
			effectiveSort = SectorModelField.MinimumX;
		}

		String effectiveDirection;
		if (direction.isPresent()) {
			effectiveDirection = direction.get();
		} else {
			effectiveDirection = SortCriteria.ASCENDING;
		}

		SectorField sortColumn = s_dbFieldMap.get(effectiveSort);

		SortCriteria sortCriteria;
		if (effectiveDirection.equals(SortCriteria.ASCENDING)) {
			sortCriteria = SortCriteria.newBuilder().ascending(sortColumn.getName()).build();
		} else {
			sortCriteria = SortCriteria.newBuilder().descending(sortColumn.getName()).build();
		}

		UriBuilder uriBuilder = getUriBuilder().pathSegment("sector");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		logger.log(Level.INFO, "uri = {0}", uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Sector>> restResponse = restTemplate.exchange(uri, HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Sector>>() {
				});

		SearchResult<Sector> searchResult = restResponse.getBody();

		ModelFactory factory = new ModelFactory();
		List<SectorModel> sectorModels = factory.createSectors(searchResult.getEntities());
		modelMap.addAttribute("sectors", sectorModels);

		PagingModel pagingModel = new PagingModel();
		pagingModel.setNextBookmark(searchResult.getEndingBookmark());
		pagingModel.setEndOfResults(searchResult.getEndOfResults());
		pagingModel.setCurrentPageNumber(pageNumber.orElse(1));
		pagingModel.setNextPageNumber(pageNumber.orElse(1) + 1);
		modelMap.addAttribute("paging", pagingModel);

		SortingModel sortingModel = new SortingModel();
		sortingModel.setSort(effectiveSort);
		sortingModel.setDirection(effectiveDirection);
		modelMap.addAttribute("sorting", sortingModel);
		modelMap.addAttribute("F", new SectorModelField());

		return "sectorList";
	}

	@GetMapping({ "/create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		SectorModel sectorModel = factory.createSector();

		modelMap.addAttribute("sector", sectorModel);

		return "sectorCreate";
	}

	@PostMapping({ "/createPost" })
	public String createPost(@ModelAttribute SectorModel sectorModel) {

		RestFactory factory = new RestFactory();
		Sector sector = factory.createSector(sectorModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("sector"), HttpMethod.POST,
				createHttpEntity(sector), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/sector/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/edit/{key}" })
	public String edit(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Sector> restResponse = restTemplate.exchange(getRestUrl("sector/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Sector>() {
				});

		ModelFactory factory = new ModelFactory();
		SectorModel sectorModel = factory.createSector(restResponse.getBody());

		modelMap.addAttribute("sector", sectorModel);

		return "sectorEdit";
	}

	@PostMapping({ "/editPost" })
	public String editPost(@ModelAttribute SectorModel sectorModel) {

		RestFactory factory = new RestFactory();
		Sector sector = factory.createSector(sectorModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("sector/" + sector.getKey()), HttpMethod.PUT, createHttpEntity(sector),
				new ParameterizedTypeReference<UpdateResult<String>>() {
				});

		return "redirect:/sector/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/delete/{key}" })
	public String delete(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Sector> restResponse = restTemplate.exchange(getRestUrl("sector/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Sector>() {
				});

		ModelFactory factory = new ModelFactory();
		SectorModel sectorModel = factory.createSector(restResponse.getBody());

		modelMap.addAttribute("sector", sectorModel);

		return "sectorDelete";
	}

	@PostMapping({ "/deletePost" })
	public String deletePost(@ModelAttribute SectorModel sectorModel) {

		RestFactory factory = new RestFactory();
		Sector sector = factory.createSector(sectorModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("sector/" + sector.getKey()), HttpMethod.DELETE, createHttpEntity(sector),
				new ParameterizedTypeReference<DeleteResult<String>>() {
				});

		return "redirect:/sector";
	}
}
