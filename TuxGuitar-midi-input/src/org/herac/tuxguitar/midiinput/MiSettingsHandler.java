package org.herac.tuxguitar.midiinput;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;

public class MiSettingsHandler implements TGPluginSettingsHandler {
	
	public MiSettingsHandler(){
		super();
	}
	
	public void openSettingsDialog(Shell parent) {
		MiConfig.instance().showDialog(parent);
	}
}
