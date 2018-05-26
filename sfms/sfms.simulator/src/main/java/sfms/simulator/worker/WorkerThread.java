package sfms.simulator.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class WorkerThread extends Thread {

	private final Logger logger = Logger.getLogger(WorkerThread.class.getName());

	public static final WorkerFunction HALT = new HaltFunction();

	private String m_name;
	private BlockingQueue<WorkerFunction> m_functionQueue;
	private Semaphore m_threadQuiescedSemaphore;

	public WorkerThread(String name, BlockingQueue<WorkerFunction> functionQueue, Semaphore threadQuiescedSemaphore) {
		if (name == null) {
			throw new IllegalArgumentException("Argument name is null.");
		}
		if (functionQueue == null) {
			throw new IllegalArgumentException("Argument messageQueue is null.");
		}
		if (threadQuiescedSemaphore == null) {
			throw new IllegalArgumentException("Argument threadQuiescedSemaphore is null.");
		}

		m_name = name;
		m_functionQueue = functionQueue;
		m_threadQuiescedSemaphore = threadQuiescedSemaphore;
	}

	@Override
	public void run() {

		try {

			logInfo("Worker thread starting.");

			WorkerFunction function = m_functionQueue.take();
			while (function != HALT) {
				function.execute();
				function = m_functionQueue.take();
			}

			logInfo("Worker thread ending.");

		} catch (InterruptedException e) {

			logInfo("Worker thread interrupted.");

		} finally {

			m_threadQuiescedSemaphore.release();

		}

	}

	private void logInfo(String text) {
		logger.info(m_name + ": " + text);
	}

	private static class HaltFunction implements WorkerFunction {

		@Override
		public void execute() {
			// No action required.
		}

	}

}