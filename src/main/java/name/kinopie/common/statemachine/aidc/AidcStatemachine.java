package name.kinopie.common.statemachine.aidc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import name.kinopie.common.statemachine.core.ActionContext;
import name.kinopie.common.statemachine.core.Statemachine;
import name.kinopie.common.statemachine.core.Trigger;

/**
 * https://www.icao.int/APAC/Documents/edocs/icd_aidc_ver3.pdf の <br/>
 * Figure D -5 Flight State Transitions Diagram を実装
 * 
 * @author Takashi Kinoshita
 */
public class AidcStatemachine extends Statemachine<FlightState, MsgId, ActionContext<FlightState, MsgId>> {

	public AidcStatemachine() {
		super(createActions());
	}

	private static Map<Trigger<FlightState, MsgId>, Function<ActionContext<FlightState, MsgId>, FlightState>> createActions() {
		Map<Trigger<FlightState, MsgId>, Function<ActionContext<FlightState, MsgId>, FlightState>> actions = new LinkedHashMap<>();

		actions.put(Trigger.when(FlightState.PreNotifying, MsgId.ABI), context -> FlightState.Notifying);

		actions.put(Trigger.when(FlightState.Notifying, MsgId.ABI), context -> FlightState.Notifying);
		actions.put(Trigger.when(FlightState.Notifying, MsgId.MAC), context -> FlightState.PreNotifying);
		actions.put(Trigger.when(FlightState.Notifying, MsgId.CPL), context -> FlightState.Negotiating);
		actions.put(Trigger.when(FlightState.Notifying, MsgId.EST), context -> FlightState.Coordinating);
		actions.put(Trigger.when(FlightState.Notifying, MsgId.PAC), context -> FlightState.Coordinating);

		actions.put(Trigger.when(FlightState.Negotiating, MsgId.CDN), context -> FlightState.Negotiating);
		actions.put(Trigger.when(FlightState.Negotiating, MsgId.ACP), context -> FlightState.Coordinated);

		actions.put(Trigger.when(FlightState.Coordinating, MsgId.ACP), context -> FlightState.Coordinated);

		actions.put(Trigger.when(FlightState.Coordinated, MsgId.MAC), context -> FlightState.PreNotifying);
		actions.put(Trigger.when(FlightState.Coordinated, MsgId.CDN), context -> FlightState.Renegotiating);
		actions.put(Trigger.when(FlightState.Coordinated, MsgId.TRU), context -> FlightState.Coordinated);
		actions.put(Trigger.when(FlightState.Coordinated, MsgId.TOC), context -> FlightState.Transferring);

		actions.put(Trigger.when(FlightState.Renegotiating, MsgId.CDN), context -> FlightState.Renegotiating);
		actions.put(Trigger.when(FlightState.Renegotiating, MsgId.ACP), context -> FlightState.Coordinated);
		actions.put(Trigger.when(FlightState.Renegotiating, MsgId.REJ), context -> FlightState.Coordinated);

		actions.put(Trigger.when(FlightState.Transferring, MsgId.AOC), context -> FlightState.Transferred);

		actions.put(Trigger.when(FlightState.Transferred, MsgId.CDN), context -> FlightState.BackwardRenegotiating);

		actions.put(Trigger.when(FlightState.BackwardRenegotiating, MsgId.CDN),
				context -> FlightState.BackwardRenegotiating);
		actions.put(Trigger.when(FlightState.BackwardRenegotiating, MsgId.REJ), context -> FlightState.Transferred);
		actions.put(Trigger.when(FlightState.BackwardRenegotiating, MsgId.ACP), context -> FlightState.Transferred);
		return actions;
	}
}
