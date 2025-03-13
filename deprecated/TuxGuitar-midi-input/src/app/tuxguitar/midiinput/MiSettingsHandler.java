package app.tuxguitar.midiinput;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.ui.widget.UIWindow;

public class MiSettingsHandler implements TGPluginSettingsHandler {

	public MiSettingsHandler(){
		super();
	}

	public void openSettingsDialog(UIWindow parent) {
		MiConfig.instance().showDialog(parent);
	}
}
