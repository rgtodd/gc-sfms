package sfms.simulator.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "sfms.simulator" })
public class SfmsSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SfmsSpringBootApplication.class, args);
	}

}