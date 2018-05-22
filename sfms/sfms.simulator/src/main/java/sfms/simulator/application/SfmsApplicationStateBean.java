package sfms.simulator.application;

import java.util.logging.Logger;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class SfmsApplicationStateBean implements ApplicationListener<ApplicationReadyEvent> {

	private final Logger logger = Logger.getLogger(SfmsApplicationStateBean.class.getName());

	public void close() {
		logger.info("Closing...");
	}

	/**
	 * This event is executed as late as conceivably possible to indicate that the
	 * application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		logger.info("Starting...");
	}
}
