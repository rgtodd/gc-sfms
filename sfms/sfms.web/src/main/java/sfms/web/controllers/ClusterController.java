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
import sfms.rest.api.models.Cluster;
import sfms.rest.api.schemas.ClusterField;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.ClusterModel;
import sfms.web.models.PagingModel;
import sfms.web.models.SortingModel;
import sfms.web.schemas.ClusterModelField;

@Controller
@RequestMapping({ "/cluster" })
public class ClusterController extends SfmsController {

	private final Logger logger = Logger.getLogger(ClusterController.class.getName());

	private static final Map<String, ClusterField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, ClusterField>();
		s_dbFieldMap.put(ClusterModelField.ClusterPartition, ClusterField.ClusterPartition);
		s_dbFieldMap.put(ClusterModelField.ClusterX, ClusterField.ClusterX);
		s_dbFieldMap.put(ClusterModelField.ClusterY, ClusterField.ClusterY);
		s_dbFieldMap.put(ClusterModelField.ClusterZ, ClusterField.ClusterZ);
		s_dbFieldMap.put(ClusterModelField.MinimumX, ClusterField.MinimumX);
		s_dbFieldMap.put(ClusterModelField.MinimumY, ClusterField.MinimumY);
		s_dbFieldMap.put(ClusterModelField.MinimumZ, ClusterField.MinimumZ);
		s_dbFieldMap.put(ClusterModelField.MaximumX, ClusterField.MaximumX);
		s_dbFieldMap.put(ClusterModelField.MaximumY, ClusterField.MaximumY);
		s_dbFieldMap.put(ClusterModelField.MaximumZ, ClusterField.MaximumZ);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Cluster> restResponse = restTemplate.exchange(getRestUrl("cluster/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Cluster>() {
				});

		ModelFactory factory = new ModelFactory();
		ClusterModel clusterModel = factory.createCluster(restResponse.getBody());

		modelMap.addAttribute("cluster", clusterModel);

		return "clusterDetail";
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
			effectiveSort = ClusterModelField.MinimumX;
		}

		String effectiveDirection;
		if (direction.isPresent()) {
			effectiveDirection = direction.get();
		} else {
			effectiveDirection = SortCriteria.ASCENDING;
		}

		ClusterField sortColumn = s_dbFieldMap.get(effectiveSort);

		SortCriteria sortCriteria;
		if (effectiveDirection.equals(SortCriteria.ASCENDING)) {
			sortCriteria = SortCriteria.newBuilder().ascending(sortColumn.getName()).build();
		} else {
			sortCriteria = SortCriteria.newBuilder().descending(sortColumn.getName()).build();
		}

		UriBuilder uriBuilder = getUriBuilder().pathSegment("cluster");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		logger.log(Level.INFO, "uri = {0}", uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<Cluster>> restResponse = restTemplate.exchange(uri, HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Cluster>>() {
				});

		SearchResult<Cluster> searchResult = restResponse.getBody();

		ModelFactory factory = new ModelFactory();
		List<ClusterModel> clusterModels = factory.createClusters(searchResult.getEntities());
		modelMap.addAttribute("clusters", clusterModels);

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
		modelMap.addAttribute("F", new ClusterModelField());

		return "clusterList";
	}

	@GetMapping({ "/create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		ClusterModel clusterModel = factory.createCluster();

		modelMap.addAttribute("cluster", clusterModel);

		return "clusterCreate";
	}

	@PostMapping({ "/createPost" })
	public String createPost(@ModelAttribute ClusterModel clusterModel) {

		RestFactory factory = new RestFactory();
		Cluster cluster = factory.createCluster(clusterModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("cluster"),
				HttpMethod.POST, createHttpEntity(cluster), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/cluster/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/edit/{key}" })
	public String edit(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Cluster> restResponse = restTemplate.exchange(getRestUrl("cluster/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Cluster>() {
				});

		ModelFactory factory = new ModelFactory();
		ClusterModel clusterModel = factory.createCluster(restResponse.getBody());

		modelMap.addAttribute("cluster", clusterModel);

		return "clusterEdit";
	}

	@PostMapping({ "/editPost" })
	public String editPost(@ModelAttribute ClusterModel clusterModel) {

		RestFactory factory = new RestFactory();
		Cluster cluster = factory.createCluster(clusterModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("cluster/" + cluster.getKey()), HttpMethod.PUT, createHttpEntity(cluster),
				new ParameterizedTypeReference<UpdateResult<String>>() {
				});

		return "redirect:/cluster/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/delete/{key}" })
	public String delete(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Cluster> restResponse = restTemplate.exchange(getRestUrl("cluster/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Cluster>() {
				});

		ModelFactory factory = new ModelFactory();
		ClusterModel clusterModel = factory.createCluster(restResponse.getBody());

		modelMap.addAttribute("cluster", clusterModel);

		return "clusterDelete";
	}

	@PostMapping({ "/deletePost" })
	public String deletePost(@ModelAttribute ClusterModel clusterModel) {

		RestFactory factory = new RestFactory();
		Cluster cluster = factory.createCluster(clusterModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("cluster/" + cluster.getKey()), HttpMethod.DELETE, createHttpEntity(cluster),
				new ParameterizedTypeReference<DeleteResult<String>>() {
				});

		return "redirect:/cluster";
	}
}
