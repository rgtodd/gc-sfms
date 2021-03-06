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
import sfms.rest.api.models.Cluster;
import sfms.rest.api.models.Star;
import sfms.rest.api.schemas.ClusterField;
import sfms.rest.api.schemas.StarField;
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
		s_dbFieldMap.put(ClusterModelField.CLUSTER_PARTITION, ClusterField.ClusterPartition);
		s_dbFieldMap.put(ClusterModelField.CLUSTER_X, ClusterField.ClusterX);
		s_dbFieldMap.put(ClusterModelField.CLUSTER_Y, ClusterField.ClusterY);
		s_dbFieldMap.put(ClusterModelField.CLUSTER_Z, ClusterField.ClusterZ);
		s_dbFieldMap.put(ClusterModelField.MINIMUM_X, ClusterField.MinimumX);
		s_dbFieldMap.put(ClusterModelField.MINIMUM_Y, ClusterField.MinimumY);
		s_dbFieldMap.put(ClusterModelField.MINIMUM_Z, ClusterField.MinimumZ);
		s_dbFieldMap.put(ClusterModelField.MAXIMUM_X, ClusterField.MaximumX);
		s_dbFieldMap.put(ClusterModelField.MAXIMUM_Y, ClusterField.MaximumY);
		s_dbFieldMap.put(ClusterModelField.MAXIMUM_Z, ClusterField.MaximumZ);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<Cluster> restResponse = restTemplate.exchange(getRestUrl("cluster/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<Cluster>() {
				});

		URI uriStarList = createStarListUri(key);
		logger.log(Level.INFO, "uriStarList = {0}", uriStarList);

		ResponseEntity<SearchResult<Star>> starResponse = restTemplate.exchange(uriStarList, HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<Star>>() {
				});

		ModelFactory factory = new ModelFactory();
		ClusterModel clusterModel = factory.createCluster(restResponse.getBody(), starResponse.getBody().getEntities());

		modelMap.addAttribute("cluster", clusterModel);

		return "clusterDetail";
	}

	@GetMapping({ "" })
	public String getList(
			@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(name = WebParameters.SORT, defaultValue = ClusterModelField.CLUSTER_XYZ) String sort,
			@RequestParam(name = WebParameters.DIRECTION, defaultValue = SortCriteria.ASCENDING) String direction,
			ModelMap modelMap) {

		URI uri = createListUri(sort, direction, bookmark);
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
		sortingModel.setSort(sort);
		sortingModel.setDirection(direction);
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
		ClusterModel clusterModel = factory.createCluster(restResponse.getBody(), null);

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
		ClusterModel clusterModel = factory.createCluster(restResponse.getBody(), null);

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

	private URI createStarListUri(String key) {

		FilterCriteria filterCriteria = FilterCriteria.newBuilder()
				.add(StarField.ClusterKey.getName(), FilterCriteria.EQ, key)
				.build();

		UriBuilder uriBuilder = getUriBuilder().pathSegment("star");
		uriBuilder.queryParam(RestParameters.FILTER, filterCriteria.toString());
		uriBuilder.queryParam(RestParameters.DETAIL, RestDetail.MINIMAL);

		URI uri = uriBuilder.build();
		return uri;
	}

	private URI createListUri(String sort, String direction, Optional<String> bookmark) {

		SortCriteria sortCriteria;
		if (sort.equals(ClusterModelField.CLUSTER_XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(ClusterField.ClusterX.getName(), direction)
					.sort(ClusterField.ClusterY.getName(), direction)
					.sort(ClusterField.ClusterZ.getName(), direction)
					.build();
		} else if (sort.equals(ClusterModelField.MINIMUM_XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(ClusterField.MinimumX.getName(), direction)
					.sort(ClusterField.MinimumY.getName(), direction)
					.sort(ClusterField.MinimumZ.getName(), direction)
					.build();
		} else if (sort.equals(ClusterModelField.MAXIMUM_XYZ)) {
			sortCriteria = SortCriteria.newBuilder()
					.sort(ClusterField.MaximumX.getName(), direction)
					.sort(ClusterField.MaximumY.getName(), direction)
					.sort(ClusterField.MaximumZ.getName(), direction)
					.build();
		} else {
			ClusterField sortColumn = s_dbFieldMap.get(sort);
			sortCriteria = SortCriteria.newBuilder()
					.sort(sortColumn.getName(), direction)
					.build();
		}

		UriBuilder uriBuilder = getUriBuilder().pathSegment("cluster");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		return uri;
	}
}
