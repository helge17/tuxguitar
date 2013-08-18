package org.herac.tuxguitar.player.impl.midiport.oss;

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
		return MidiPlugin.MODULE_ID;
	}
	
	public MidiOutputPortProviderImpl findMidiOutputPortProvider(){
		MidiOutputPortProviderPlugin plugin = findMidiOutputPortProviderPlugin();
		if( plugin != null ){
			return (MidiOutputPortProviderImpl) plugin.getProvider();
		}
		return null;
	}
	
	private MidiOutputPortProviderPlugin findMidiOutputPortProviderPlugin(){
		List pluginInstances = TGPluginManager.getInstance().getPluginInstances(MidiOutputPortProviderPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return ((MidiOutputPortProviderPlugin)pluginInstances.get(0));
		}
		return null;
	}
}
