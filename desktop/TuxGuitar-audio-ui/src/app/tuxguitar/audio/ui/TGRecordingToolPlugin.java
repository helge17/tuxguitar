package app.tuxguitar.audio.ui;

import app.tuxguitar.util.TGContext;

/**
 * Tools > Recording...: the Recording window (recording state, Record, input meters of the armed tracks).
 */
public class TGRecordingToolPlugin extends TGAudioToolPlugin {

	public TGRecordingToolPlugin() {
		super("audio.status");
	}

	protected void doAction(TGContext context) {
		new TGRecordingWindow(context).open();
	}
}
