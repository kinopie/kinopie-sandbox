package name.kinopie.common.statemachine.core;

public interface ActionContext<S, E> {

	S getCurrentState();

	E getEvent();
}
