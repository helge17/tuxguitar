package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.List;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.plugin.TGPluginException;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler getHandler() throws TGPluginException {
		return new MidiSettingsHandler(this);
	}
	
	public String getModuleId() {
		return MidiSynthPlugin.MODULE_ID;
	}
	
	public MidiOutputPortSettings findMidiSettings(){
		MidiOutputPortProviderPlugin plugin = findMidiOutputPortProviderPlugin();
		if( plugin != null ){
			return plugin.getProviderImpl().getSettings();
		}
		return null;
	}
	
	private MidiOutputPortProviderPlugin findMidiOutputPortProviderPlugin(){
		List pluginInstances = TGPluginManager.getInstance(getContext()).getPluginInstances(MidiOutputPortProviderPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return ((MidiOutputPortProviderPlugin)pluginInstances.get(0));
		}
		return null;
	}
}
