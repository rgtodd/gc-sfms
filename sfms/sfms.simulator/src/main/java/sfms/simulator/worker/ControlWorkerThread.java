package sfms.simulator.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class ControlWorkerThread extends Thread {

	private final Logger logger = Logger.getLogger(ControlWorkerThread.class.getName());

	private BlockingQueue<ControlWorkerMessage> m_messageQueue;
	private Semaphore m_threadQuiescedSemaphore;

	public ControlWorkerThread(BlockingQueue<ControlWorkerMessage> messageQueue, Semaphore threadQuiescedSemaphore) {
		if (messageQueue == null) {
			throw new IllegalArgumentException("Argument messageQueue is null.");
		}
		if (threadQuiescedSemaphore == null) {
			throw new IllegalArgumentException("Argument threadQuiescedSemaphore is null.");
		}

		m_messageQueue = messageQueue;
		m_threadQuiescedSemaphore = threadQuiescedSemaphore;
	}

	@Override
	public void run() {

		try {

			logger.info("Control worker thread starting.");

			ControlWorkerMessage message = m_messageQueue.take();
			while (message.getCommand() != ControlWorkerMessageCommand.HALT) {
				processMessage(message);
				message = m_messageQueue.take();
			}

			logger.info("Control worker thread ending.");

		} catch (InterruptedException e) {

			logger.info("Control worker thread interrupted.");

		} finally {

			m_threadQuiescedSemaphore.release();

		}

	}

	private void processMessage(ControlWorkerMessage message) {

	}

}
