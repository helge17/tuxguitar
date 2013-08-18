package org.herac.tuxguitar.player.impl.jsa;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.impl.jsa.sequencer.MidiSequencerProviderPlugin;

public class MidiPluginList extends TGPluginList {
	
	protected List getPlugins() {
		List plugins = new ArrayList();
		plugins.add(new MidiOutputPortProviderPlugin());
		plugins.add(new MidiSequencerProviderPlugin());
		return plugins;
	}
	
	public String getModuleId() {
		return MidiPlugin.MODULE_ID;
	}
}
