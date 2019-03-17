package name.kinopie.common.statemachine.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StatemachineTest {
	private Statemachine<String, String, ActionContext<String, String>, Function<ActionContext<String, String>, String>> statemachine;
	private String state1 = "state1";
	private String event1 = "event1";
	private String state2 = "state2";
	private String event2 = "event2";

	private String state3 = "state3";
	
	@Before
	public void setUp() throws Exception {
		statemachine = new Statemachine<>();
	}

	@After
	public void tearDown() throws Exception {
		statemachine = null;
	}

	@Test
	public void testNullContextFactory() {
		try {
			new Statemachine<Object, Object, ActionContext<Object, Object>, Function<ActionContext<Object, Object>, Object>>(
					null);
			fail();
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), is("contextFactory is marked @NonNull but is null"));
		}
	}

	@Test
	public void testNormalEntry() {
		TestAction action = new TestAction();
		statemachine.entry(state1, event1, action);
		statemachine.send(state1, event1);
		assertThat(action.getExecutionCount(), is(1));
	}

	@Test
	public void testNormalEntry2() {
		statemachine.entry(state1, event1, context -> state2, context -> state3);
		statemachine.send(state1, event1);
		assertThat(statemachine.send(state1, event1), is(state3));
	}
	
	@Test
	public void testNoEntry_UnresolvableTriggerException() {
		TestAction action = new TestAction();
		statemachine.entry(state1, event1, action);
		try {
			statemachine.send(state2, event2);
			fail();
		} catch (UnresolvableTriggerException e) {
			assertThat(e.getTrigger(), is(new Trigger<>(state2, event2)));
			assertThat(e.getStatemachine(), is(statemachine));
			assertThat(e.getMessage(), is(
					"Statemachine(actionsForTrigger={Trigger(state=state1, event=event1)=[TestAction]}, fallbacks=[]) cannot resolve Trigger(state=state2, event=event2)"));
		}
	}

	@Test
	public void testEntry_DuplicateAction1() {
		TestAction action = new TestAction();
		statemachine.entry(state1, event1, action);
		statemachine.entry(state1, event1, action);
		statemachine.send(state1, event1);
		assertThat(action.getExecutionCount(), is(2));
	}

	@Test
	public void testEntry_DuplicateAction2() {
		TestAction action = new TestAction();
		statemachine.entry(state1, event1, action, action);
		statemachine.send(state1, event1);
		assertThat(action.getExecutionCount(), is(2));
	}

	@Test
	public void testEntry_EmptyAction() {
		statemachine.entry(state1, event1);
		statemachine.send(state1, event1);
	}

	@Test
	public void testEntry_ActionIsNull() {
		try {
			statemachine.entry(state1, event1, null);
			fail();
		} catch (NullPointerException e) {
			assertThat(e.getMessage(), is("actions is marked @NonNull but is null"));
		}
	}

	@Test
	public void testEntry_ActionsContainNull() {
		try {
			statemachine.entry(state1, event1, (Function<ActionContext<String, String>, String>) null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("actions cannot contain null"));
		}
	}

	@Test
	public void testFallback() {
		TestAction fallback = new TestAction();
		statemachine.fallback(fallback);
		statemachine.send(state1, event1);
		assertThat(fallback.getExecutionCount(), is(1));
	}

	@Test
	public void testFallback_ContainNull() {
		try {
			statemachine.fallback((Function<ActionContext<String, String>, String>) null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("fallbacks cannot contain null"));
		}
	}
}

class TestAction implements Function<ActionContext<String, String>, String> {
	private int executionCount = 0;

	@Override
	public String apply(ActionContext<String, String> context) {
		executionCount++;
		return context.getState();
	}

	public int getExecutionCount() {
		return executionCount;
	}

	@Override
	public String toString() {
		return "TestAction";
	}
}