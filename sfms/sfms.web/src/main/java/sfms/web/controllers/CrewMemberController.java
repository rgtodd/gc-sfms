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
import sfms.rest.api.models.CrewMember;
import sfms.rest.api.schemas.CrewMemberField;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.PagingModel;
import sfms.web.models.SortingModel;
import sfms.web.schemas.CrewMemberModelField;

@Controller
@RequestMapping({ "/crewMember" })
public class CrewMemberController extends SfmsController {

	private final Logger logger = Logger.getLogger(CrewMemberController.class.getName());

	private static final Map<String, CrewMemberField> s_dbFieldMap;
	static {
		s_dbFieldMap = new HashMap<String, CrewMemberField>();
		s_dbFieldMap.put(CrewMemberModelField.LAST_NAME, CrewMemberField.LastName);
		s_dbFieldMap.put(CrewMemberModelField.FIRST_NAME, CrewMemberField.FirstName);
	}

	@GetMapping({ "/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(getRestUrl("crewMember/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberDetail";
	}

	@GetMapping({ "" })
	public String getList(
			@RequestParam(WebParameters.PAGE_NUMBER) Optional<Integer> pageNumber,
			@RequestParam(WebParameters.BOOKMARK) Optional<String> bookmark,
			@RequestParam(name = WebParameters.SORT, defaultValue = CrewMemberModelField.LAST_NAME) String sort,
			@RequestParam(name = WebParameters.DIRECTION, defaultValue = SortCriteria.ASCENDING) String direction,
			ModelMap modelMap) {

		URI uri = createListUri(sort, direction, bookmark);
		logger.log(Level.INFO, "uri = {0}", uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<CrewMember>> restResponse = restTemplate.exchange(uri, HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<SearchResult<CrewMember>>() {
				});

		SearchResult<CrewMember> searchResult = restResponse.getBody();

		ModelFactory factory = new ModelFactory();
		List<CrewMemberModel> crewMemberModels = factory.createCrewMembers(searchResult.getEntities());
		modelMap.addAttribute("crewMembers", crewMemberModels);

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
		modelMap.addAttribute("F", new CrewMemberModelField());

		return "crewMemberList";
	}

	@GetMapping({ "/create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember();

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberCreate";
	}

	@PostMapping({ "/createPost" })
	public String createPost(@ModelAttribute CrewMemberModel crewMemberModel) {

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(crewMemberModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("crewMember"),
				HttpMethod.POST, createHttpEntity(crewMember), new ParameterizedTypeReference<CreateResult<String>>() {
				});

		return "redirect:/crewMember/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/edit/{key}" })
	public String edit(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(getRestUrl("crewMember/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberEdit";
	}

	@PostMapping({ "/editPost" })
	public String editPost(@ModelAttribute CrewMemberModel crewMemberModel) {

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(crewMemberModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("crewMember/" + crewMember.getKey()), HttpMethod.PUT, createHttpEntity(crewMember),
				new ParameterizedTypeReference<UpdateResult<String>>() {
				});

		return "redirect:/crewMember/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/delete/{key}" })
	public String delete(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(getRestUrl("crewMember/" + key), HttpMethod.GET,
				createHttpEntity(), new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberDelete";
	}

	@PostMapping({ "/deletePost" })
	public String deletePost(@ModelAttribute CrewMemberModel crewMemberModel) {

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(crewMemberModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("crewMember/" + crewMember.getKey()), HttpMethod.DELETE, createHttpEntity(crewMember),
				new ParameterizedTypeReference<DeleteResult<String>>() {
				});

		return "redirect:/crewMember";
	}

	private URI createListUri(String sort, String direction, Optional<String> bookmark) {

		CrewMemberField sortColumn = s_dbFieldMap.get(sort);
		SortCriteria sortCriteria = SortCriteria.newBuilder()
				.sort(sortColumn.getName(), direction)
				.build();

		UriBuilder uriBuilder = getUriBuilder().pathSegment("crewMember");
		if (bookmark.isPresent()) {
			uriBuilder.queryParam(RestParameters.BOOKMARK, bookmark.get());
		}
		uriBuilder.queryParam(RestParameters.SORT, sortCriteria.toString());

		URI uri = uriBuilder.build();
		return uri;
	}
}
