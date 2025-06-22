package app.tuxguitar.player.impl.midiport.audiounit.settings;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.player.impl.midiport.audiounit.utils.MidiConfigUtils;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class MidiSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;

	public MidiSettingsHandler(TGContext context){
		this.context = context;
	}

	public void openSettingsDialog(UIWindow parent) {
		MidiConfigUtils.setupDialog(this.context, parent);
	}
}
