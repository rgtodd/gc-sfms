package sfms.rest.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sfms.rest.Throttle;

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
