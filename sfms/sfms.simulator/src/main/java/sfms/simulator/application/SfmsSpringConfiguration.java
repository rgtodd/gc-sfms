package sfms.simulator.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sfms.common.application.AppEngineHeaderFilter;
import sfms.simulator.worker.Worker;

@Configuration
public class SfmsSpringConfiguration implements ApplicationContextAware {

	private static final String CONTROL = "Control";
	private static final String TRANSACTION = "Transaction";

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

	@Bean
	public Worker controlWorker() {
		return new Worker(CONTROL);
	}

	@Bean
	public Worker transactionWorker() {
		return new Worker(TRANSACTION);
	}
}