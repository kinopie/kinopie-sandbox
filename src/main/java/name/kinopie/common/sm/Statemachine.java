package name.kinopie.common.sm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Statemachine<S, E> {
	private Map<Trigger<S, E>, Action<S, E>> actions;
	private Action<S, E> fallback;

	public Statemachine() {
		this(new LinkedHashMap<Trigger<S, E>, Action<S, E>>());
	}

	public Statemachine(Map<Trigger<S, E>, Action<S, E>> actions) {
		this(actions, null);
	}

	public Statemachine(Map<Trigger<S, E>, Action<S, E>> actions, Action<S, E> fallback) {
		this.actions = actions;
		this.fallback = fallback;
	}

	public S send(S currentState, E event) {
		// FIXME 固有の例外を送出するほうがベターかも
		Objects.requireNonNull(currentState, "Argument 'state' must not be null.");
		Objects.requireNonNull(event, "Argument 'event' must not be null.");
		Trigger<S, E> trigger = new Trigger<>(currentState, event);
		Action<S, E> action = actions.getOrDefault(trigger, fallback);
		if (action == null) {
			throw new UnresolvableTriggerException(this, trigger);
		}
		return action.execute(currentState, event);
	}

	public void entry(S currentState, E event, Action<S, E> action) {
		Objects.requireNonNull(currentState, "Argument 'state' must not be null.");
		Objects.requireNonNull(event, "Argument 'event' must not be null.");
		Objects.requireNonNull(action, "Argument 'action' must not be null.");
		Trigger<S, E> trigger = new Trigger<>(currentState, event);
		if (actions.containsKey(trigger)) {
			// TODO WARNログでエントリの重複を通知
		}
		actions.put(trigger, action);
	}

	@Override
	public String toString() {
		return "Statemachine [actions=" + actions + ", fallback=" + fallback + "]";
	}
}
