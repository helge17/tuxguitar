package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.tools.custom.TGToolItemPlugin;
import app.tuxguitar.song.models.TGTrack;

/**
 * An item of the Tools menu of TuxGuitar's audio, named by its message key.
 */
abstract class TGAudioToolPlugin extends TGToolItemPlugin {

	private final String item;

	TGAudioToolPlugin(String item) {
		this.item = item;
	}

	protected String getItemName() {
		return this.item;
	}

	public String getModuleId() {
		return TGAudioPlugin.MODULE_ID;
	}

	// the caret's track: none before a song is shown
	static TGTrack caretTrack() {
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
	}
}
