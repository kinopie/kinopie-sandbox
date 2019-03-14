package name.kinopie.common.statemachine.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import lombok.ToString;

@ToString(of = { "actions", "fallback" })
public class Statemachine<S, E, C extends ActionContext<S, E>> {
	private Map<Trigger<S, E>, Function<C, S>> actions = new LinkedHashMap<>();
	private BiFunction<S, E, C> contextFactory;
	private Function<C, S> fallback;

	@SuppressWarnings("unchecked")
	public Statemachine() {
		this((s, e) -> (C) new ActionContext<S, E>() {
			@Override
			public S getState() {
				return s;
			}

			@Override
			public E getEvent() {
				return e;
			}
		});
	}

	public Statemachine(@NonNull BiFunction<S, E, C> contextFactory) {
		this.contextFactory = contextFactory;
	}

	public S send(S state, E event) {
		Trigger<S, E> trigger = new Trigger<>(state, event);
		Function<C, S> action = actions.getOrDefault(trigger, fallback);
		if (action == null) {
			throw new UnresolvableTriggerException(this, trigger);
		}
		C context = contextFactory.apply(state, event);
		return action.apply(context);
	}

	public void entry(S state, E event, @NonNull Function<C, S> action) {
		Trigger<S, E> trigger = new Trigger<>(state, event);
		if (actions.containsKey(trigger)) {
			// TODO WARNログでエントリの重複を通知
		}
		actions.put(trigger, action);
	}

	public void fallback(Function<C, S> fallback) {
		this.fallback = fallback;
	}
}
