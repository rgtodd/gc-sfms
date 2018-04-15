package sfms.web.controllers;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sfms.web.SfmsController;

@Controller
@RequestMapping({ "/explore" })
public class ExploreController extends SfmsController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(ExploreController.class.getName());

	@GetMapping({ "" })
	public String explore(ModelMap modelMap) {

		return "explore";
	}
}
