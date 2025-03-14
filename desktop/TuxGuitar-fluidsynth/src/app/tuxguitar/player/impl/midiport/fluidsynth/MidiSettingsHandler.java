package app.tuxguitar.player.impl.midiport.fluidsynth;

import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

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
