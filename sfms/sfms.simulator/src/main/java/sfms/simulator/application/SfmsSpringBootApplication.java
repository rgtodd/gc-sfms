package sfms.simulator.application;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "sfms.simulator" })
public class SfmsSpringBootApplication {

	private final Logger logger = Logger.getLogger(SfmsSpringBootApplication.class.getName());

	@PostConstruct
	public void postConstruct() throws Exception {
		logger.info("SfmsSpringBootApplication started.");
	}

	@PreDestroy
	public void preDestroy() {
		logger.info("SfmsSpringBootApplication stopped.");
	}

	public static void main(String[] args) {
		SpringApplication.run(SfmsSpringBootApplication.class, args);
	}

}