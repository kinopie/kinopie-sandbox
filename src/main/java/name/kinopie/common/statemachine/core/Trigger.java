package name.kinopie.common.statemachine.core;

public class Trigger<S, E> {
	private S state;
	private E event;

	private Trigger(S state, E event) {
		this.state = state;
		this.event = event;
	}

	public S getState() {
		return state;
	}

	public E getEvent() {
		return event;
	}

	public static <S, E> Trigger<S, E> when(S state, E event) {
		return new Trigger<S, E>(state, event);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trigger other = (Trigger) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Trigger [state=" + state + ", event=" + event + "]";
	}
}
