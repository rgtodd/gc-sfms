package sfms.simulator.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SfmsSpringConfiguration implements ApplicationContextAware {

	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Bean(destroyMethod = "close")
	public SfmsApplicationStateBean getSfmsApplicationStateBean() {
		return new SfmsApplicationStateBean();
	}

}