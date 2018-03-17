package sfms.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping({ "/", "/home" })
	public String get(ModelMap modelMap) {

		return "home";
	}
}
