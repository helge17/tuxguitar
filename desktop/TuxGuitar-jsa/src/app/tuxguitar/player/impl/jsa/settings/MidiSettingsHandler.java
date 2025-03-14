package app.tuxguitar.player.impl.jsa.settings;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;
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
