package name.kinopie.common.statemachine.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class StatemachineException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private Statemachine<?, ?, ?> statemachine;
}
