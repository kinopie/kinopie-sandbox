package name.kinopie.common.statemachine.core;

import java.util.function.Function;

import lombok.Getter;

@Getter
public class DuplicateEntryException extends StatemachineException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private Trigger<?, ?> trigger;
	private Function<?, ?> action;

	public DuplicateEntryException(Statemachine<?, ?, ?> statemachine, Trigger<?, ?> trigger, Function<?, ?> action) {
		super(statemachine);
		this.trigger = trigger;
		this.action = action;
	}

	@Override
	public String getMessage() {
		return String.format("%s has duplicate entry %s,%s.", getStatemachine(), getTrigger());
	}
}
