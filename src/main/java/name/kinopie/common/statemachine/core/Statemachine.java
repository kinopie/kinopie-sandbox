package name.kinopie.common.statemachine.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import lombok.ToString;

@ToString(of = { "actionsForTrigger", "fallbacks" })
public class Statemachine<S, E, C extends ActionContext<S, E>, A extends Function<C, S>> {
	private Map<Trigger<S, E>, List<A>> actionsForTrigger = new LinkedHashMap<>();
	private BiFunction<S, E, C> contextFactory;
	private List<A> fallbacks = new ArrayList<>();

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
		if (actionsForTrigger.containsKey(trigger)) {
			Collection<A> actions = actionsForTrigger.get(trigger);
			return apply(state, event, actions);
		} else if (!fallbacks.isEmpty()) {
			return apply(state, event, fallbacks);
		} else {
			throw new UnresolvableTriggerException(this, trigger);
		}
	}

	private S apply(S initial, E event, Collection<A> actions) {
		S current = initial;
		for (Function<C, S> action : actions) {
			C context = contextFactory.apply(current, event);
			current = action.apply(context);
		}
		return current;
	}

	@SafeVarargs
	public final void entry(S state, E event, @NonNull A... actions) {
		if (Arrays.asList(actions).contains(null)) {
			throw new IllegalArgumentException("actions cannot contain null");
		}
		Trigger<S, E> trigger = new Trigger<>(state, event);
		List<A> registered = actionsForTrigger.getOrDefault(trigger, new ArrayList<>());
		Arrays.stream(actions).forEach(action -> {
			if (registered.contains(action)) {
				// TODO WARNログでエントリの重複を通知
			}
			registered.add(action);
		});
		actionsForTrigger.put(trigger, registered);
	}

	@SafeVarargs
	public final void fallback(A... fallbacks) {
		if (Arrays.asList(fallbacks).contains(null)) {
			throw new IllegalArgumentException("fallbacks cannot contain null");
		}
		Arrays.stream(fallbacks).forEach(fallback -> {
			if (this.fallbacks.contains(fallback)) {
				// TODO WARNログでエントリの重複を通知
			}
			this.fallbacks.add(fallback);
		});
	}
}
