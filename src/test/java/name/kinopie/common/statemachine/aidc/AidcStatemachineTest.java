package name.kinopie.common.statemachine.aidc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import name.kinopie.common.statemachine.core.ActionContext;
import name.kinopie.common.statemachine.core.Statemachine;
import name.kinopie.common.statemachine.core.Trigger;
import name.kinopie.common.statemachine.core.UnresolvableTriggerException;

public class AidcStatemachineTest {

	private Statemachine<FlightState, MsgId, ActionContext<FlightState, MsgId>> aidcStatemachine;

	@Before
	public void setUp() throws Exception {
		aidcStatemachine = new AidcStatemachineSupplier().get();
	}

	@After
	public void tearDown() throws Exception {
		aidcStatemachine = null;
	}

	@Test
	public void test() {
		assertThat(aidcStatemachine.send(FlightState.PreNotifying, MsgId.ABI), is(FlightState.Notifying));
		assertThat(aidcStatemachine.send(FlightState.Notifying, MsgId.ABI), is(FlightState.Notifying));
		assertThat(aidcStatemachine.send(FlightState.Notifying, MsgId.MAC), is(FlightState.PreNotifying));
		assertThat(aidcStatemachine.send(FlightState.Notifying, MsgId.CPL), is(FlightState.Negotiating));
		assertThat(aidcStatemachine.send(FlightState.Notifying, MsgId.EST), is(FlightState.Coordinating));
		assertThat(aidcStatemachine.send(FlightState.Notifying, MsgId.PAC), is(FlightState.Coordinating));

		assertThat(aidcStatemachine.send(FlightState.Negotiating, MsgId.CDN), is(FlightState.Negotiating));
		assertThat(aidcStatemachine.send(FlightState.Negotiating, MsgId.ACP), is(FlightState.Coordinated));

		assertThat(aidcStatemachine.send(FlightState.Coordinating, MsgId.ACP), is(FlightState.Coordinated));

		assertThat(aidcStatemachine.send(FlightState.Coordinated, MsgId.MAC), is(FlightState.PreNotifying));
		assertThat(aidcStatemachine.send(FlightState.Coordinated, MsgId.CDN), is(FlightState.Renegotiating));
		assertThat(aidcStatemachine.send(FlightState.Coordinated, MsgId.TRU), is(FlightState.Coordinated));
		assertThat(aidcStatemachine.send(FlightState.Coordinated, MsgId.TOC), is(FlightState.Transferring));

		assertThat(aidcStatemachine.send(FlightState.Renegotiating, MsgId.CDN), is(FlightState.Renegotiating));
		assertThat(aidcStatemachine.send(FlightState.Renegotiating, MsgId.ACP), is(FlightState.Coordinated));
		assertThat(aidcStatemachine.send(FlightState.Renegotiating, MsgId.REJ), is(FlightState.Coordinated));

		assertThat(aidcStatemachine.send(FlightState.Transferring, MsgId.AOC), is(FlightState.Transferred));

		assertThat(aidcStatemachine.send(FlightState.Transferred, MsgId.CDN), is(FlightState.BackwardRenegotiating));

		assertThat(aidcStatemachine.send(FlightState.BackwardRenegotiating, MsgId.CDN),
				is(FlightState.BackwardRenegotiating));
		assertThat(aidcStatemachine.send(FlightState.BackwardRenegotiating, MsgId.REJ), is(FlightState.Transferred));
		assertThat(aidcStatemachine.send(FlightState.BackwardRenegotiating, MsgId.ACP), is(FlightState.Transferred));
	}

	@Test
	public void testUnresolvableTriggerException() {
		try {
			aidcStatemachine.send(FlightState.PreNotifying, MsgId.TOC);
			fail();
		} catch (UnresolvableTriggerException e) {
			assertThat(e.getTrigger(), is(new Trigger<>(FlightState.PreNotifying, MsgId.TOC)));
			assertThat(e.getStatemachine(), is(aidcStatemachine));
		}
	}
}
