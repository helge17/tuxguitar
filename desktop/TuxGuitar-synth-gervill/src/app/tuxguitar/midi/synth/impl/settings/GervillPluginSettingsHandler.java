package app.tuxguitar.midi.synth.impl.settings;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.midi.synth.impl.settings.GervillConfigUtils;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class GervillPluginSettingsHandler implements TGPluginSettingsHandler {

	private TGContext context;

	public GervillPluginSettingsHandler(TGContext context){
		this.context = context;
	}

	public void openSettingsDialog(UIWindow parent) {
		GervillConfigUtils.setupDialog(this.context, parent);
	}
}
