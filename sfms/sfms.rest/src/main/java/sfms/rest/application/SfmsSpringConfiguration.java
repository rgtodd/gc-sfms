package sfms.rest.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sfms.rest.Throttle;

/**
 * Spring configuration class that defines the Spring beans used by the
 * application.
 *
 */
@Configuration
public class SfmsSpringConfiguration {

	@Bean
	public Throttle throttle() {
		return new Throttle();
	}

	@Bean
	public AppEngineHeaderFilter appEngineHeaderFilter() {
		return new AppEngineHeaderFilter();
	}
}
