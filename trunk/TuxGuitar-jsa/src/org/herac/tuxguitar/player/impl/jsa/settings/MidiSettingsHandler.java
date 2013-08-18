package org.herac.tuxguitar.player.impl.jsa.settings;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;

public class MidiSettingsHandler implements TGPluginSettingsHandler {
	
	public MidiSettingsHandler(){
		super();
	}
	
	public void openSettingsDialog(Shell parent) {
		MidiConfigUtils.setupDialog(parent);
	}
}
