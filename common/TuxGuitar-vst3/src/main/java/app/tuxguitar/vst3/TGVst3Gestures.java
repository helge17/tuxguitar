package app.tuxguitar.vst3;

/**
 * Hears a plugin's edits from its own editor as gestures: a gesture runs from the first begin edit to the end edit that
 * closes it (a preset chosen begins the preset and every parameter it sets, then ends them all), and when it ends the
 * plugin holds what the gesture did.
 */
public final class TGVst3Gestures implements TGVst3Listener {

	private final Runnable ended;
	// the edits begun and not ended yet
	private int open;

	// ended: run when a gesture ends
	public TGVst3Gestures(Runnable ended) {
		this.ended = ended;
	}

	public void notice(TGVst3Notice notice) {
		if (notice.getKind() == TGVst3Notice.Kind.BEGIN_EDIT) {
			this.open++;
		} else if (notice.getKind() == TGVst3Notice.Kind.END_EDIT && --this.open == 0) {
			this.ended.run();
		}
	}
}
