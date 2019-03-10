package name.kinopie.common.sm;

@FunctionalInterface
public interface Action<S, E> {

	S execute(S current, E event);
}
