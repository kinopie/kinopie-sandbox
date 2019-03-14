package name.kinopie.common.statemachine.core;

public interface ActionContext<S, E> {

	S getState();

	E getEvent();
}
