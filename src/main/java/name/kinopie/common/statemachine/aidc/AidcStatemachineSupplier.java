package name.kinopie.common.statemachine.aidc;

import java.util.function.Function;
import java.util.function.Supplier;

import name.kinopie.common.statemachine.core.ActionContext;
import name.kinopie.common.statemachine.core.Statemachine;

/**
 * https://www.icao.int/APAC/Documents/edocs/icd_aidc_ver3.pdf の <br/>
 * Figure D -5 Flight State Transitions Diagram を実装
 */
public class AidcStatemachineSupplier implements
		Supplier<Statemachine<FlightState, MsgId, ActionContext<FlightState, MsgId>, Function<ActionContext<FlightState, MsgId>, FlightState>>> {

	@Override
	public Statemachine<FlightState, MsgId, ActionContext<FlightState, MsgId>, Function<ActionContext<FlightState, MsgId>, FlightState>> get() {
		Statemachine<FlightState, MsgId, ActionContext<FlightState, MsgId>, Function<ActionContext<FlightState, MsgId>, FlightState>> aidcStatemachine = new Statemachine<>();
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

		aidcStatemachine.entry(FlightState.BackwardRenegotiating, MsgId.CDN,
				context -> FlightState.BackwardRenegotiating);
		aidcStatemachine.entry(FlightState.BackwardRenegotiating, MsgId.REJ, context -> FlightState.Transferred);
		aidcStatemachine.entry(FlightState.BackwardRenegotiating, MsgId.ACP, context -> FlightState.Transferred);
		return aidcStatemachine;
	}
}
