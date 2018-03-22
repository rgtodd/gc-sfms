package sfms.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import sfms.rest.Secret;
import sfms.web.SfmsController;
import sfms.web.models.DebugEntryModel;

@Controller
public class DebugController extends SfmsController {

	@GetMapping({ "/debug" })
	public String get(ModelMap modelMap) {

		List<DebugEntryModel> debugEntries = new ArrayList<DebugEntryModel>();

		DebugEntryModel debugEntry = new DebugEntryModel();
		debugEntry.setId("RestAuthorizationToken");
		debugEntry.setValue(Secret.getRestAuthorizationToken());
		debugEntries.add(debugEntry);

		modelMap.addAttribute("debugEntries", debugEntries);

		return "debug";
	}
}
