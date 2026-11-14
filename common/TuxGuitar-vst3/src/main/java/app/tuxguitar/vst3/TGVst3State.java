package app.tuxguitar.vst3;

/**
 * What a plugin saves: its component's state and, when its controller is apart from it, the controller's.
 */
public final class TGVst3State {

	private final byte[] component;
	private final byte[] controller;

	public TGVst3State(byte[] component, byte[] controller) {
		this.component = component;
		this.controller = controller;
	}

	public byte[] getComponent() {
		return this.component;
	}

	public byte[] getController() {
		return this.controller;
	}
}
