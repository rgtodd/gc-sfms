package sfms.simulator.application;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import sfms.simulator.worker.ControlWorker;

@Component
public class SfmsApplicationStateBean {

	private final Logger logger = Logger.getLogger(SfmsApplicationStateBean.class.getName());

	private ControlWorker m_controlWorker;

	@PostConstruct
	public void start() {
		logger.info("Application starting.");

		m_controlWorker = new ControlWorker();
		m_controlWorker.start();
	}

	@PreDestroy
	public void stop() {
		logger.info("Application stopping.");

		m_controlWorker.stop();
		m_controlWorker = null;
	}
}
