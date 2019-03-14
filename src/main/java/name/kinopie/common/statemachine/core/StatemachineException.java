package name.kinopie.common.statemachine.core;

public abstract class StatemachineException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private Statemachine<?, ?, ?> statemachine;

	public StatemachineException(Statemachine<?, ?, ?> statemachine) {
		this.statemachine = statemachine;
	}

	public Statemachine<?, ?, ?> getStatemachine() {
		return statemachine;
	}
}
