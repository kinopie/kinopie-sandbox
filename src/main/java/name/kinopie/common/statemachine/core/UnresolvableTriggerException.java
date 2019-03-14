package name.kinopie.common.statemachine.core;

public class UnresolvableTriggerException extends StatemachineException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private Trigger<?, ?> trigger;

	UnresolvableTriggerException(Statemachine<?, ?, ?> statemachine, Trigger<?, ?> trigger) {
		super(statemachine);
		this.trigger = trigger;
	}

	public Trigger<?, ?> getTrigger() {
		return trigger;
	}

	@Override
	public String getMessage() {
		return String.format("%s cannot resolve %s", getStatemachine(), getTrigger());
	}
}
