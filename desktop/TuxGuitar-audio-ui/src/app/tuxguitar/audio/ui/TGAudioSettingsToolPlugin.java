package app.tuxguitar.audio.ui;

import app.tuxguitar.util.TGContext;

/**
 * Tools > Audio Device...: the audio device settings.
 */
public class TGAudioSettingsToolPlugin extends TGAudioToolPlugin {

	public TGAudioSettingsToolPlugin() {
		super("audio.settings");
	}

	protected void doAction(TGContext context) {
		new TGAudioSettingsDialog(context).open();
	}
}
