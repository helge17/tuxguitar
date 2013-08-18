package org.herac.tuxguitar.player.impl.midiport.oss;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;

public class MidiSettingsHandler implements TGPluginSettingsHandler {
	
	private MidiSettingsPlugin midiSettingsPlugin;
	
	public MidiSettingsHandler(MidiSettingsPlugin midiSettingsPlugin){
		this.midiSettingsPlugin = midiSettingsPlugin;
	}
	
	public void openSettingsDialog(Shell parent) {
		MidiConfigUtils.setupDialog(parent, this.midiSettingsPlugin.findMidiOutputPortProvider());
	}
}
