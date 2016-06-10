package org.herac.tuxguitar.midiinput;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class MiSettingsHandler implements TGPluginSettingsHandler {
	
	public MiSettingsHandler(){
		super();
	}
	
	public void openSettingsDialog(UIWindow parent) {
		MiConfig.instance().showDialog(parent);
	}
}
