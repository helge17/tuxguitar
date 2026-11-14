package app.tuxguitar.audio.ui;

import app.tuxguitar.app.tools.custom.TGToolItemPlugin;

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
}
