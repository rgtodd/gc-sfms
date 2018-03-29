package sfms.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import sfms.rest.CreateResult;
import sfms.rest.Secret;
import sfms.rest.models.CrewMember;
import sfms.rest.models.Spaceship;
import sfms.rest.test.ValueGenerator;
import sfms.web.SfmsController;
import sfms.web.models.DebugEntryModel;
import sfms.web.models.DebugGenerateOptionsModel;

@Controller
public class DebugController extends SfmsController {

	@GetMapping({ "/debug" })
	public String debug(ModelMap modelMap) {

		List<DebugEntryModel> debugEntries = new ArrayList<DebugEntryModel>();

		DebugEntryModel debugEntry = new DebugEntryModel();
		debugEntry.setId("RestAuthorizationToken");
		debugEntry.setValue(Secret.getRestAuthorizationToken());
		debugEntries.add(debugEntry);

		modelMap.addAttribute("debugEntries", debugEntries);

		return "debug";
	}

	@GetMapping({ "/debug_generateSpaceships" })
	public String generateSpaceships(ModelMap modelMap) {

		DebugGenerateOptionsModel options = new DebugGenerateOptionsModel();

		modelMap.addAttribute("options", options);

		return "debugGenerateSpaceships";
	}

	@PostMapping({ "/debug_generateSpaceshipsPost" })
	public String generateSpaceshipsPost(@ModelAttribute DebugGenerateOptionsModel options) {

		RestTemplate restTemplate = createRestTempate();

		for (int idx = 0; idx < options.getRecordCount(); ++idx) {
			String name = "USS " + ValueGenerator.getRandomAdjective() + " " + ValueGenerator.getRandomNoun();

			Spaceship spaceship = new Spaceship();
			spaceship.setName(name);

			@SuppressWarnings("unused")
			ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("spaceship"),
					HttpMethod.PUT,
					createHttpEntity(spaceship), new ParameterizedTypeReference<CreateResult<String>>() {
					});
		}

		return "redirect:/debug";
	}

	@GetMapping({ "/debug_generateCrewMembers" })
	public String generateCrewMembers(ModelMap modelMap) {

		DebugGenerateOptionsModel options = new DebugGenerateOptionsModel();

		modelMap.addAttribute("options", options);

		return "debugGenerateCrewMembers";
	}

	@PostMapping({ "/debug_generateCrewMembersPost" })
	public String generateCrewMembersPost(@ModelAttribute DebugGenerateOptionsModel options) {

		RestTemplate restTemplate = createRestTempate();

		for (int idx = 0; idx < options.getRecordCount(); ++idx) {
			CrewMember crewMember = new CrewMember();
			crewMember.setFirstName(ValueGenerator.getRandomFirstName());
			crewMember.setLastName(ValueGenerator.getRandomLastName());

			@SuppressWarnings("unused")
			ResponseEntity<CreateResult<String>> restResponse = restTemplate.exchange(getRestUrl("crewMember"),
					HttpMethod.PUT,
					createHttpEntity(crewMember), new ParameterizedTypeReference<CreateResult<String>>() {
					});
		}

		return "redirect:/debug";
	}
}
