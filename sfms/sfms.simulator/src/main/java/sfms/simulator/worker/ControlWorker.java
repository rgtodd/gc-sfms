package sfms.simulator.worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlWorker {

	private final Logger logger = Logger.getLogger(ControlWorker.class.getName());

	private static final int QUEUE_CAPACITY = 100;
	private static final int TIMEOUT = 60;
	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

	private BlockingQueue<ControlWorkerMessage> m_messageQueue;
	private Semaphore m_threadQuiescedSemaphore;
	private ControlWorkerThread m_thread;

	public void start() {
		if (m_thread != null) {
			throw new IllegalStateException("Worker already open.");
		}

		logger.info("Starting control worker.");

		m_messageQueue = new ArrayBlockingQueue<ControlWorkerMessage>(QUEUE_CAPACITY);
		m_threadQuiescedSemaphore = new Semaphore(0);
		m_thread = new ControlWorkerThread(m_messageQueue, m_threadQuiescedSemaphore);
		m_thread.start();

		logger.info("Control worker started.");
	}

	public void stop() {

		logger.info("Stopping control worker.");

		if (m_thread != null) {
			try {
				logger.info("Quiescing worker thread.");
				quiesce();
			} catch (InterruptedException | TimeoutException e) {
				logger.log(Level.SEVERE, "Error quiescing working thread.", e);
				m_thread.interrupt();
			} finally {
				m_thread = null;
				m_threadQuiescedSemaphore = null;
				m_messageQueue = null;
			}
		}

		logger.info("Control worker stopped.");
	}

	public void quiesce() throws InterruptedException, TimeoutException {

		if (!m_thread.isAlive()) {
			logger.info("Worker thread is no longer alive.");
			return;
		}

		logger.info("Sending HALT message to worker thread.");
		boolean timeout = m_messageQueue.offer(ControlWorkerMessage.HALT, TIMEOUT, TIMEOUT_UNIT);
		if (timeout) {
			logger.severe("Could not send HALT message.");
		}

		if (!m_thread.isAlive()) {
			logger.info("Worker thread is no longer alive.");
			return;
		}

		logger.info("Waiting for worker thread to end.");
		timeout = m_threadQuiescedSemaphore.tryAcquire(1, TIMEOUT, TIMEOUT_UNIT);
		if (timeout) {
			if (!m_thread.isAlive()) {
				logger.severe("Worker thread is no longer alive.");
				return;
			}
			throw new TimeoutException("Thread did not quiesce.");
		}

		logger.info("Worker thread quiesced.");
	}

}
