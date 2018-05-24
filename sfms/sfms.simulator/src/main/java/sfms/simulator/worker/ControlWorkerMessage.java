package sfms.simulator.worker;

public class ControlWorkerMessage {

	public static final ControlWorkerMessage HALT = new ControlWorkerMessage(ControlWorkerMessageCommand.HALT);

	private ControlWorkerMessageCommand m_command;

	public ControlWorkerMessage(ControlWorkerMessageCommand command) {
		m_command = command;
	}

	public ControlWorkerMessageCommand getCommand() {
		return m_command;
	}
}
