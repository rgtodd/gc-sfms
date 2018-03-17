package sfms.web.controllers;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import sfms.rest.DeleteResult;
import sfms.rest.SearchResult;
import sfms.rest.UpdateResult;
import sfms.rest.models.CrewMember;
import sfms.web.ModelFactory;
import sfms.web.RestFactory;
import sfms.web.SfmsProperties;
import sfms.web.models.CrewMemberModel;

@Controller
public class CrewMemberController {

	@GetMapping({ "/crewMember/{id}" })
	public String get(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(
				getCrewMemberRestUrl("crewMember/" + id.toString()), HttpMethod.GET, null,
				new ParameterizedTypeReference<CrewMember>() {
				});

		ModelFactory factory = new ModelFactory();
		CrewMemberModel crewMemberModel = factory.createCrewMember(restResponse.getBody());

		modelMap.addAttribute("crewMember", crewMemberModel);

		return "crewMemberDetail";
	}

	@GetMapping({ "/crewMember" })
	public String getList(ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<SearchResult<CrewMember>> restResponse = restTemplate.exchange(
				getCrewMemberRestUrl("crewMember"), HttpMethod.GET, null,
				new ParameterizedTypeReference<SearchResult<CrewMember>>() {
				});

		ModelFactory factory = new ModelFactory();
		List<CrewMemberModel> crewMemberModels = factory.createCrewMembers(restResponse.getBody().getEntities());

		modelMap.addAttribute("crewMembers", crewMemberModels);

		return "crewMemberList";
	}

	@GetMapping({ "/crewMember_edit/{id}" })
	public String edit(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(
				getCrewMemberRestUrl("crewMember/" + id.toString()), HttpMethod.GET, null,
				new ParameterizedTypeReference<CrewMember>() {
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

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<UpdateResult<Long>> restResponse = restTemplate.exchange(
				getCrewMemberRestUrl("crewMember/" + crewMember.getId().toString()), HttpMethod.POST,
				new HttpEntity<>(crewMember), new ParameterizedTypeReference<UpdateResult<Long>>() {
				}

		);

		return "redirect:/crewMember/" + restResponse.getBody().getKey().toString();
	}

	@GetMapping({ "/crewMember_delete/{id}" })
	public String delete(@PathVariable Long id, ModelMap modelMap) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<CrewMember> restResponse = restTemplate.exchange(
				getCrewMemberRestUrl("crewMember/" + id.toString()), HttpMethod.GET, null,
				new ParameterizedTypeReference<CrewMember>() {
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

		RestTemplate restTemplate = new RestTemplate();
		@SuppressWarnings("unused")
		ResponseEntity<DeleteResult<Long>> restResponse = restTemplate.exchange(
				getCrewMemberRestUrl("crewMember/" + crewMember.getId().toString()), HttpMethod.DELETE,
				new HttpEntity<>(crewMember), new ParameterizedTypeReference<DeleteResult<Long>>() {
				}

		);

		return "redirect:/crewMember";
	}

	private String getCrewMemberRestUrl(String url) {
		String host = SfmsProperties.INSTANCE.getProperty(SfmsProperties.APPLICATION, SfmsProperties.SFMS_REST_HOST);
		return host + "/" + url;
	}
}
