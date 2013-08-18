package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;

public class MidiSettingsHandler implements TGPluginSettingsHandler {
	
	private MidiSettingsPlugin midiSettingsPlugin;
	
	public MidiSettingsHandler(MidiSettingsPlugin midiSettingsPlugin){
		this.midiSettingsPlugin = midiSettingsPlugin;
	}
	
	public void openSettingsDialog(Shell parent) {
		MidiOutputPortSettings midiSettings = this.midiSettingsPlugin.findMidiSettings();
		if( midiSettings != null ){
			midiSettings.configure(parent);
		}
	}
}
