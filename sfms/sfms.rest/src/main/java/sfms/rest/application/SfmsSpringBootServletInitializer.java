package sfms.rest.application;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Specifies the configuration used to run this Spring application.
 *
 */
public class SfmsSpringBootServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SfmsSpringBootApplication.class);
	}
}