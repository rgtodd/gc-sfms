package sfms.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import sfms.web.SfmsController;

@Controller
public class HomeController extends SfmsController {

	@RequestMapping({ "/", "/home" })
	public String get(ModelMap modelMap) {

		return "home";
	}
}
