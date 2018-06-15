package sfms.simulator.worker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;

public class Worker {

	private final Logger logger = Logger.getLogger(Worker.class.getName());

	private static final int QUEUE_CAPACITY = 100;
	private static final int TIMEOUT = 60;
	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

	private String m_name;

	private BlockingQueue<WorkerFunction> m_functionQueue;
	private Semaphore m_threadQuiescedSemaphore;
	private WorkerThread m_thread;

	public Worker(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Argument name is null.");
		}

		m_name = name;
	}

	public String getName() {
		return m_name;
	}

	public void start() {
		if (m_thread != null) {
			throw new IllegalStateException("Worker already open.");
		}

		logInfo("Starting control worker.");

		m_functionQueue = new ArrayBlockingQueue<WorkerFunction>(QUEUE_CAPACITY);
		m_threadQuiescedSemaphore = new Semaphore(0);
		m_thread = new WorkerThread(m_name, m_functionQueue, m_threadQuiescedSemaphore);
		m_thread.start();

		logInfo("Control worker started.");
	}

	public void stop() {
		if (m_thread != null) {
			logInfo("Stopping control worker.");

			try {
				logInfo("Quiescing worker thread.");
				quiesce();
			} catch (InterruptedException | TimeoutException e) {
				logSevere("Error quiescing working thread.", e);
				m_thread.interrupt();
			} finally {
				m_thread = null;
				m_threadQuiescedSemaphore = null;
				m_functionQueue = null;
			}

			logInfo("Control worker stopped.");
		}
	}

	public boolean isActive() {
		return m_thread != null;
	}

	public int getRequestCount() {
		if (m_functionQueue == null) {
			return 0;
		}
		return m_functionQueue.size();
	}

	public void process(WorkerFunction function) throws InterruptedException, TimeoutException {
		boolean success = m_functionQueue.offer(function, TIMEOUT, TIMEOUT_UNIT);
		if (!success) {
			throw new TimeoutException("Could not send function to worker thread.");
		}
	}

	@PreDestroy
	public void onPreDestroy() {
		stop();
	}

	private void quiesce() throws InterruptedException, TimeoutException {

		logInfo("Sending HALT to worker thread.");
		boolean success = m_functionQueue.offer(WorkerThread.HALT, TIMEOUT, TIMEOUT_UNIT);
		if (!success) {
			throw new TimeoutException("Could not send HALT message to worker thread.");
		}

		logInfo("Waiting for worker thread to end.");
		success = m_threadQuiescedSemaphore.tryAcquire(1, TIMEOUT, TIMEOUT_UNIT);
		if (!success) {
			throw new TimeoutException("Could not confirm worker thread shutdown.");
		}

		logInfo("Worker thread quiesced.");
	}

	private void logInfo(String text) {
		logger.info(m_name + ": " + text);
	}

	private void logSevere(String text, Throwable t) {
		logger.log(Level.SEVERE, m_name + ": " + text, t);
	}
}
