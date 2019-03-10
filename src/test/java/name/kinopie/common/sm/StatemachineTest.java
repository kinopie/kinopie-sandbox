package name.kinopie.common.sm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StatemachineTest {
	private Statemachine<FlightState, Message> flightSm;

	@Before
	public void setUp() throws Exception {
		// https://www.icao.int/APAC/Documents/edocs/icd_aidc_ver3.pdf の
		// Figure D -5 Flight State Transitions Diagram を実装
		flightSm = new Statemachine<>();
		flightSm.entry(FlightState.PreNotifying, Message.ABI, (s, e) -> FlightState.Notifying);

		flightSm.entry(FlightState.Notifying, Message.ABI, (cs, e) -> FlightState.Notifying);
		flightSm.entry(FlightState.Notifying, Message.MAC, (cs, e) -> FlightState.PreNotifying);
		flightSm.entry(FlightState.Notifying, Message.CPL, (cs, e) -> FlightState.Negotiating);
		flightSm.entry(FlightState.Notifying, Message.EST, (cs, e) -> FlightState.Coordinating);
		flightSm.entry(FlightState.Notifying, Message.PAC, (cs, e) -> FlightState.Coordinating);

		flightSm.entry(FlightState.Negotiating, Message.CDN, (cs, e) -> FlightState.Negotiating);
		flightSm.entry(FlightState.Negotiating, Message.ACP, (cs, e) -> FlightState.Coordinated);

		flightSm.entry(FlightState.Coordinating, Message.ACP, (cs, e) -> FlightState.Coordinated);

		flightSm.entry(FlightState.Coordinated, Message.MAC, (cs, e) -> FlightState.PreNotifying);
		flightSm.entry(FlightState.Coordinated, Message.CDN, (cs, e) -> FlightState.Renegotiating);
		flightSm.entry(FlightState.Coordinated, Message.TRU, (cs, e) -> FlightState.Coordinated);
		flightSm.entry(FlightState.Coordinated, Message.TOC, (cs, e) -> FlightState.Transferring);

		flightSm.entry(FlightState.Renegotiating, Message.CDN, (cs, e) -> FlightState.Renegotiating);
		flightSm.entry(FlightState.Renegotiating, Message.ACP, (cs, e) -> FlightState.Coordinated);
		flightSm.entry(FlightState.Renegotiating, Message.REJ, (cs, e) -> FlightState.Coordinated);

		flightSm.entry(FlightState.Transferring, Message.AOC, (cs, e) -> FlightState.Transferred);

		flightSm.entry(FlightState.Transferred, Message.CDN, (cs, e) -> FlightState.BackwardRenegotiating);

		flightSm.entry(FlightState.BackwardRenegotiating, Message.CDN, (cs, e) -> FlightState.BackwardRenegotiating);
		flightSm.entry(FlightState.BackwardRenegotiating, Message.REJ, (cs, e) -> FlightState.Transferred);
		flightSm.entry(FlightState.BackwardRenegotiating, Message.ACP, (cs, e) -> FlightState.Transferred);
	}

	@After
	public void tearDown() throws Exception {
		flightSm = null;
	}

	@Test
	public void test() {
		assertThat(flightSm.send(FlightState.PreNotifying, Message.ABI), is(FlightState.Notifying));
		assertThat(flightSm.send(FlightState.Notifying, Message.ABI), is(FlightState.Notifying));
		assertThat(flightSm.send(FlightState.Notifying, Message.MAC), is(FlightState.PreNotifying));
		assertThat(flightSm.send(FlightState.Notifying, Message.CPL), is(FlightState.Negotiating));
		assertThat(flightSm.send(FlightState.Notifying, Message.EST), is(FlightState.Coordinating));
		assertThat(flightSm.send(FlightState.Notifying, Message.PAC), is(FlightState.Coordinating));

		assertThat(flightSm.send(FlightState.Negotiating, Message.CDN), is(FlightState.Negotiating));
		assertThat(flightSm.send(FlightState.Negotiating, Message.ACP), is(FlightState.Coordinated));

		assertThat(flightSm.send(FlightState.Coordinating, Message.ACP), is(FlightState.Coordinated));

		assertThat(flightSm.send(FlightState.Coordinated, Message.MAC), is(FlightState.PreNotifying));
		assertThat(flightSm.send(FlightState.Coordinated, Message.CDN), is(FlightState.Renegotiating));
		assertThat(flightSm.send(FlightState.Coordinated, Message.TRU), is(FlightState.Coordinated));
		assertThat(flightSm.send(FlightState.Coordinated, Message.TOC), is(FlightState.Transferring));

		assertThat(flightSm.send(FlightState.Renegotiating, Message.CDN), is(FlightState.Renegotiating));
		assertThat(flightSm.send(FlightState.Renegotiating, Message.ACP), is(FlightState.Coordinated));
		assertThat(flightSm.send(FlightState.Renegotiating, Message.REJ), is(FlightState.Coordinated));

		assertThat(flightSm.send(FlightState.Transferring, Message.AOC), is(FlightState.Transferred));

		assertThat(flightSm.send(FlightState.Transferred, Message.CDN), is(FlightState.BackwardRenegotiating));

		assertThat(flightSm.send(FlightState.BackwardRenegotiating, Message.CDN),
				is(FlightState.BackwardRenegotiating));
		assertThat(flightSm.send(FlightState.BackwardRenegotiating, Message.REJ), is(FlightState.Transferred));
		assertThat(flightSm.send(FlightState.BackwardRenegotiating, Message.ACP), is(FlightState.Transferred));
	}

	@Test
	public void testUnresolvableTriggerException() {
		try {
			flightSm.send(FlightState.PreNotifying, Message.TOC);
			fail();
		} catch (UnresolvableTriggerException e) {
			assertThat(e.getTrigger(), is(new Trigger<>(FlightState.PreNotifying, Message.TOC)));
			assertThat(e.getStatemachine(), is(flightSm));
		}
	}
}

enum FlightState {
	PreNotifying, Notifying, Negotiating, Coordinating, Coordinated, Renegotiating, Transferring, Transferred,
	BackwardRenegotiating;
}

enum Message {
	ABI, MAC, CPL, EST, PAC, CDN, ACP, REJ, TRU, TOC, AOC
}