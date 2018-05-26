package sfms.simulator.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

	@RequestMapping("/")
	public String home() {
		return "Hello Docker World - Version 3";
	}
}