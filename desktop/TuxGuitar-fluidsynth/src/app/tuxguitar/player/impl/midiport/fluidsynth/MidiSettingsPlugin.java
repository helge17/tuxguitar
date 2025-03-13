package app.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.List;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;
import app.tuxguitar.util.plugin.TGPluginManager;

public class MidiSettingsPlugin extends TGPluginSettingsAdapter {

	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new MidiSettingsHandler(context, this);
	}

	public String getModuleId() {
		return MidiSynthPlugin.MODULE_ID;
	}

	public MidiOutputPortSettings findMidiSettings(TGContext context){
		MidiOutputPortProviderPlugin plugin = findMidiOutputPortProviderPlugin(context);
		if( plugin != null ){
			return plugin.getProviderImpl(context).getSettings();
		}
		return null;
	}

	private MidiOutputPortProviderPlugin findMidiOutputPortProviderPlugin(TGContext context){
		List<MidiOutputPortProviderPlugin> pluginInstances = TGPluginManager.getInstance(context).getPluginInstances(MidiOutputPortProviderPlugin.class);
		if( pluginInstances != null && !pluginInstances.isEmpty() ){
			return pluginInstances.get(0);
		}
		return null;
	}
}
