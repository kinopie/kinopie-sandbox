package name.kinopie.common.statemachine.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Statemachine<S, E, C extends ActionContext<S, E>> {
	private Map<Trigger<S, E>, Function<C, S>> actions = new LinkedHashMap<>();
	@SuppressWarnings("unchecked")
	private BiFunction<S, E, C> contextFactory = (s, e) -> (C) new ActionContext<S, E>() {
		@Override
		public S getCurrentState() {
			return s;
		}

		@Override
		public E getEvent() {
			return e;
		}
	};
	private Function<C, S> fallback;

	public Statemachine() {
	}

	public Statemachine(Map<Trigger<S, E>, Function<C, S>> actions) {
		this.actions = Objects.requireNonNull(actions, "actions must not be null.");
	}

	public Statemachine(BiFunction<S, E, C> contextFactory) {
		this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory must not be null.");
	}

	public Statemachine(Map<Trigger<S, E>, Function<C, S>> actions, BiFunction<S, E, C> contextFactory) {
		this.actions = Objects.requireNonNull(actions, "actions must not be null.");
		this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory must not be null.");
	}

	public S send(S currentState, E event) {
		// FIXME 固有の例外を送出するほうがベターかも
		Objects.requireNonNull(currentState, "Argument 'state' must not be null.");
		Objects.requireNonNull(event, "Argument 'event' must not be null.");
		Trigger<S, E> trigger = Trigger.when(currentState, event);
		Function<C, S> action = actions.getOrDefault(trigger, fallback);
		if (action == null) {
			throw new UnresolvableTriggerException(this, trigger);
		}
		C context = contextFactory.apply(currentState, event);
		return action.apply(context);
	}

	public void entry(S currentState, E event, Function<C, S> action) {
		Objects.requireNonNull(currentState, "Argument 'state' must not be null.");
		Objects.requireNonNull(event, "Argument 'event' must not be null.");
		Objects.requireNonNull(action, "Argument 'action' must not be null.");
		Trigger<S, E> trigger = Trigger.when(currentState, event);
		if (actions.containsKey(trigger)) {
			// TODO WARNログでエントリの重複を通知
		}
		actions.put(trigger, action);
	}

	public void fallback(Function<C, S> fallback) {
		this.fallback = fallback;
	}

	@Override
	public String toString() {
		return "Statemachine [actions=" + actions + ", fallback=" + fallback + "]";
	}
}
