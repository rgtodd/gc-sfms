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

import sfms.rest.CreateResult;
import sfms.rest.DeleteResult;
import sfms.rest.SearchResult;
import sfms.rest.UpdateResult;
import sfms.rest.models.CrewMember;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsController;
import sfms.web.models.CrewMemberModel;
import sfms.web.models.PagingModel;

@Controller
public class CrewMemberController extends SfmsController {

	private final Logger logger = Logger.getLogger(CrewMemberController.class.getName());

	@GetMapping({ "/crewMember/{key}" })
	public String get(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(getRestUrl("crewMember/" + key),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberDetail";
	}

	@GetMapping({ "/crewMember" })
	public String getList(
			@RequestParam("pageNumber") Optional<Integer> pageNumber,
			@RequestParam("bookmark") Optional<String> bookmark,
			ModelMap modelMap) {

		UriBuilder uriBuilder = getUriBuilder().pathSegment("crewMember");
		if (bookmark.isPresent()) {
			uriBuilder = uriBuilder.queryParam("bookmark", bookmark.get());
		}

		URI uri = uriBuilder.build();
		logger.log(Level.INFO, "uri = {0}", uri);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<SearchResult<CrewMember>> restResponse = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				createHttpEntity(),
				new ParameterizedTypeReference<SearchResult<CrewMember>>() {
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

		return "crewMemberList";
	}

	@GetMapping({ "/crewMember_create" })
	public String create(ModelMap modelMap) {

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember();

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberCreate";
	}

	@PostMapping({ "/crewMember_createPost" })
	public String createPost(@ModelAttribute CrewMemberModel crewMemberModel) {

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(crewMemberModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("crewMember"),
				HttpMethod.PUT, createHttpEntity(crewMember), new ParameterizedTypeReference<CreateResult<String>>() {
				}

		);

		return "redirect:/crewMember/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/crewMember_edit/{key}" })
	public String edit(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(getRestUrl("crewMember/" + key),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberEdit";
	}

	@PostMapping({ "/crewMember_editPost" })
	public String editPost(@ModelAttribute CrewMemberModel crewMemberModel) {

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(crewMemberModel);

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<UpdateResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("crewMember/" + crewMember.getKey()), HttpMethod.PUT, createHttpEntity(crewMember),
				new ParameterizedTypeReference<UpdateResult<String>>() {
				}

		);

		return "redirect:/crewMember/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/crewMember_delete/{key}" })
	public String delete(@PathVariable String key, ModelMap modelMap) {

		RestTemplate restTemplate = createRestTempate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(getRestUrl("crewMember/" + key),
				HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberDelete";
	}

	@PostMapping({ "/crewMember_deletePost" })
	public String deletePost(@ModelAttribute CrewMemberModel crewMemberModel) {

		RestFactory factory = new RestFactory();
		CrewMember crewMember = factory.createCrewMember(crewMemberModel);

		RestTemplate restTemplate = createRestTempate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<String>> restResponse = restTemplate.exchange(
				getRestUrl("crewMember/" + crewMember.getKey()), HttpMethod.DELETE,
				createHttpEntity(crewMember), new ParameterizedTypeReference<DeleteResult<String>>() {
				}

		);

		return "redirect:/crewMember";
	}
}
