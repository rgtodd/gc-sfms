package sfms.simulator.application;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sfms.simulator.worker.Worker;

@Component
public class SfmsApplicationStateBean {

	private static final Logger LOGGER = Logger.getLogger(SfmsApplicationStateBean.class.getName());

	@Autowired
	private Worker controlWorker;

	@Autowired
	private Worker transactionWorker;

	@PostConstruct
	public void onPostConstruct() {
		LOGGER.info("Application starting.");

		controlWorker.start();
		transactionWorker.start();
	}

	@PreDestroy
	public void onPreDestroy() {
		LOGGER.info("Application stopping.");

		controlWorker.stop();
		transactionWorker.stop();
	}
}
