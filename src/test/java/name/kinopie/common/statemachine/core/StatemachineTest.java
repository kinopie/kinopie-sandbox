package name.kinopie.common.statemachine.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import name.kinopie.common.statemachine.aidc.FlightState;
import name.kinopie.common.statemachine.aidc.MsgId;
import name.kinopie.common.statemachine.core.ActionContext;
import name.kinopie.common.statemachine.core.Statemachine;
import name.kinopie.common.statemachine.core.Trigger;
import name.kinopie.common.statemachine.core.UnresolvableTriggerException;

public class StatemachineTest {
	private Statemachine<FlightState, MsgId, ActionContext<FlightState, MsgId>> aidcStatemachine;

	@Before
	public void setUp() throws Exception {
		// https://www.icao.int/APAC/Documents/edocs/icd_aidc_ver3.pdf の
		// Figure D -5 Flight State Transitions Diagram を実装
		aidcStatemachine = new Statemachine<>();
		aidcStatemachine.entry(FlightState.PreNotifying, MsgId.ABI, context -> FlightState.Notifying);

		aidcStatemachine.entry(FlightState.Notifying, MsgId.ABI, context -> FlightState.Notifying);
		aidcStatemachine.entry(FlightState.Notifying, MsgId.MAC, context -> FlightState.PreNotifying);
		aidcStatemachine.entry(FlightState.Notifying, MsgId.CPL, context -> FlightState.Negotiating);
		aidcStatemachine.entry(FlightState.Notifying, MsgId.EST, context -> FlightState.Coordinating);
		aidcStatemachine.entry(FlightState.Notifying, MsgId.PAC, context -> FlightState.Coordinating);

		aidcStatemachine.entry(FlightState.Negotiating, MsgId.CDN, context -> FlightState.Negotiating);
		aidcStatemachine.entry(FlightState.Negotiating, MsgId.ACP, context -> FlightState.Coordinated);

		aidcStatemachine.entry(FlightState.Coordinating, MsgId.ACP, context -> FlightState.Coordinated);

		aidcStatemachine.entry(FlightState.Coordinated, MsgId.MAC, context -> FlightState.PreNotifying);
		aidcStatemachine.entry(FlightState.Coordinated, MsgId.CDN, context -> FlightState.Renegotiating);
		aidcStatemachine.entry(FlightState.Coordinated, MsgId.TRU, context -> FlightState.Coordinated);
		aidcStatemachine.entry(FlightState.Coordinated, MsgId.TOC, context -> FlightState.Transferring);

		aidcStatemachine.entry(FlightState.Renegotiating, MsgId.CDN, context -> FlightState.Renegotiating);
		aidcStatemachine.entry(FlightState.Renegotiating, MsgId.ACP, context -> FlightState.Coordinated);
		aidcStatemachine.entry(FlightState.Renegotiating, MsgId.REJ, context -> FlightState.Coordinated);

		aidcStatemachine.entry(FlightState.Transferring, MsgId.AOC, context -> FlightState.Transferred);

		aidcStatemachine.entry(FlightState.Transferred, MsgId.CDN, context -> FlightState.BackwardRenegotiating);

		aidcStatemachine.entry(FlightState.BackwardRenegotiating, MsgId.CDN, context -> FlightState.BackwardRenegotiating);
		aidcStatemachine.entry(FlightState.BackwardRenegotiating, MsgId.REJ, context -> FlightState.Transferred);
		aidcStatemachine.entry(FlightState.BackwardRenegotiating, MsgId.ACP, context -> FlightState.Transferred);
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
			assertThat(e.getTrigger(), is(Trigger.when(FlightState.PreNotifying, MsgId.TOC)));
			assertThat(e.getStatemachine(), is(aidcStatemachine));
		}
	}
}
