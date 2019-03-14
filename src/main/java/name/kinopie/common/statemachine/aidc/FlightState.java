package name.kinopie.common.statemachine.aidc;

public enum FlightState {
	PreNotifying, Notifying, Negotiating, Coordinating, Coordinated, Renegotiating, Transferring, Transferred,
	BackwardRenegotiating;
}
