package app.tuxguitar.audio.ui;

import app.tuxguitar.util.TGContext;

/**
 * Tools > Sound Chain...: the caret's track's sound chain (greyed while there is no track).
 */
public class TGSoundChainToolPlugin extends TGAudioToolPlugin {

	public TGSoundChainToolPlugin() {
		super("audio.sound-chain");
	}

	protected boolean isEnabled(TGContext context) {
		return caretTrack() != null;
	}

	protected void doAction(TGContext context) {
		new TGSoundChainDialog(context, caretTrack()).open();
	}
}
