package app.tuxguitar.vst3;

/**
 * What a plugin told its host: an edit from its own editor (begin, perform with the parameter's plain value, end; a
 * gesture runs from the first begin to the last end), or a restart (its latency changed, say), with the flags.
 */
public final class TGVst3Notice {

	public enum Kind {
		BEGIN_EDIT, PERFORM_EDIT, END_EDIT, RESTART
	}

	private final Kind kind;
	private final int parameter;
	private final double plain;

	public TGVst3Notice(Kind kind, int parameter, double plain) {
		this.kind = kind;
		this.parameter = parameter;
		this.plain = plain;
	}

	public Kind getKind() {
		return this.kind;
	}

	// the parameter's id, or a restart's flags
	public int getParameter() {
		return this.parameter;
	}

	public double getPlain() {
		return this.plain;
	}
}
