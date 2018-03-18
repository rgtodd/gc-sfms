package sfms.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SfmsSpringConfiguration {

	@Bean
	public Throttle throttle() {
		return new Throttle();
	}

}
