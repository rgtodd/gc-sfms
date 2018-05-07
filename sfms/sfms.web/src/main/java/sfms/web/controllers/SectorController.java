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
import sfms.rest.api.FilterCriteria;
import sfms.rest.api.RestDetail;
import sfms.rest.api.RestParameters;
import sfms.rest.api.SearchResult;
import sfms.rest.api.SortCriteria;
import sfms.rest.api.UpdateResult;
import sfms.rest.api.models.Sector;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.SectorField;
import sfms.rest.api.schemas.StarField;
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
		s_dbFieldMap.put(SectorModelField.SECTOR_X, SectorField.SectorX);
		s_dbFieldMap.put(SectorModelField.SECTOR_Y, SectorField.SectorY);
		s_dbFieldMap.put(SectorModelField.SECTOR_Z, SectorField.SectorZ);
		s_dbFieldMap.put(SectorModelField.MINIMUM_X, SectorField.MinimumX);
		s_dbFieldMap.put(SectorModelField.MINIMUM_Y, SectorField.MinimumY);
		s_dbFieldMap.put(SectorModelField.MINIMUM_Z, SectorField.MinimumZ);
		s_dbFieldMap.put(SectorModelField.MAXIMUM_X, SectorField.MaximumX);
		s_dbFieldMap.put(SectorModelField.MAXIMUM_Y, SectorField.MaximumY);
		s_dbFieldMap.put(SectorModelField.MAXIMUM_Z, SectorField.MaximumZ);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Sector> sectorResponse = restTemplate.exchange(getRestUrl("sector/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Sector>() {
				});

		URI uriStarList = createStarListUri(key);
		logger.log(Level.INFO, "uriStarList = {0}", uriStarList);

		ResponseEntity<SearchResult<Star>> starResponse = restTemplate.exchange(uriStarList, HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Star>>() {
				});

		ModelFactory factory = new ModelFactory();
		SectorModel sectorModel = factory.createSector(sectorResponse.getBody(), starResponse.getBody().getEntities());

		modelMap.addAttribute("sector", sectorModel);

		return "sectorDetail";
	}

	@GetMapping({ "" })
	public String getList(
			@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(name = WebParameters.SORT, defaultValue = SectorModelField.SECTOR_XYZ) String sort,
			@RequestParam(name = WebParameters.DIRECTION, defaultValue = SortCriteria.ASCENDING) String direction,
			ModelMap modelMap) {

		URI uri = createSectorListUri(sort, direction, bookmark);
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
		sortingModel.setSort(sort);
		sortingModel.setDirection(direction);
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
		SectorModel sectorModel = factory.createSector(restResponse.getBody(), null);

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
		SectorModel sectorModel = factory.createSector(restResponse.getBody(), null);

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

	private URI createStarListUri(String key) {

		FilterCriteria filterCriteria = FilterCriteria.newBuilder()
				.add(StarField.SectorKey.getName(), FilterCriteria.EQ, key)
				.build();

		UriBuilder uriBuilder = getUriBuilder().pathSegment("star");
		uriBuilder.queryParam(RestParameters.FILTER, filterCriteria.toString());
		uriBuilder.queryParam(RestParameters.DETAIL, RestDetail.MINIMAL);

		URI uri = uriBuilder.build();
		return uri;
	}

	private URI createSectorListUri(String sort, String direction, Optional<String> bookmark) {

		SortCriteria sortCriteria;
		if (sort.equals(SectorModelField.SECTOR_XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(SectorField.SectorX.getName(), direction)
					.sort(SectorField.SectorY.getName(), direction)
					.sort(SectorField.SectorZ.getName(), direction)
					.build();
		} else if (sort.equals(SectorModelField.MINIMUM_XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(SectorField.MinimumX.getName(), direction)
					.sort(SectorField.MinimumY.getName(), direction)
					.sort(SectorField.MinimumZ.getName(), direction)
					.build();
		} else if (sort.equals(SectorModelField.MAXIMUM_XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(SectorField.MaximumX.getName(), direction)
					.sort(SectorField.MaximumY.getName(), direction)
					.sort(SectorField.MaximumZ.getName(), direction)
					.build();
		} else {
			SectorField sortColumn = s_dbFieldMap.get(sort);
			sortCriteria = SortCriteria.newBuilder()
					.sort(sortColumn.getName(), direction)
					.build();
		}

		UriBuilder uriBuilder = getUriBuilder().pathSegment("sector");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		return uri;
	}
}
