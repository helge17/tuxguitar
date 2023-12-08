package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class MidiSettingsHandler implements TGPluginSettingsHandler {
	
	private TGContext context;
	private MidiSettingsPlugin midiSettingsPlugin;
	
	public MidiSettingsHandler(TGContext context, MidiSettingsPlugin midiSettingsPlugin){
		this.context = context;
		this.midiSettingsPlugin = midiSettingsPlugin;
	}
	
	public void openSettingsDialog(UIWindow parent) {
		MidiOutputPortSettings midiSettings = this.midiSettingsPlugin.findMidiSettings(this.context);
		if( midiSettings != null ){
			midiSettings.configure(parent);
		}
	}
}
