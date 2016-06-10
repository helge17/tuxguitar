package org.herac.tuxguitar.player.impl.midiport.oss;

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
		MidiConfigUtils.setupDialog(this.context, parent, this.midiSettingsPlugin.findMidiOutputPortProvider(this.context));
	}
}
