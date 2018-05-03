package sfms.rest.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Provides main method used to start the Spring application.
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { ErrorMvcAutoConfiguration.class })
@ComponentScan(basePackages = { "sfms.rest" })
public class SfmsSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SfmsSpringBootApplication.class, args);
	}
}
