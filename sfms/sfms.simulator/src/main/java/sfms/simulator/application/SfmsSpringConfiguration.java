package sfms.simulator.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sfms.common.application.AppEngineHeaderFilter;

@Configuration
public class SfmsSpringConfiguration implements ApplicationContextAware {

	@SuppressWarnings("unused")
	private ApplicationContext m_applicationContext;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		m_applicationContext = applicationContext;
	}

	@Bean
	public AppEngineHeaderFilter appEngineHeaderFilter() {
		return new AppEngineHeaderFilter();
	}
}