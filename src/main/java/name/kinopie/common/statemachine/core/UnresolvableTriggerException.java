package name.kinopie.common.statemachine.core;

import lombok.Getter;

@Getter
public class UnresolvableTriggerException extends StatemachineException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private Trigger<?, ?> trigger;

	public UnresolvableTriggerException(Statemachine<?, ?, ?> statemachine, Trigger<?, ?> trigger) {
		super(statemachine);
		this.trigger = trigger;
	}

	@Override
	public String getMessage() {
		return String.format("%s cannot resolve %s", getStatemachine(), getTrigger());
	}
}
