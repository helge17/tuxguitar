package app.tuxguitar.vst3;

/**
 * A VST3 host request that failed: refused by the host process (the reason it gave), or lost with the process.
 */
public class TGVst3Exception extends Exception {

	private static final long serialVersionUID = 1L;

	public TGVst3Exception(String message) {
		super(message);
	}

	public TGVst3Exception(String message, Throwable cause) {
		super(message, cause);
	}
}
