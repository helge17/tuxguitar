package org.herac.tuxguitar.player.impl.jsa;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiSequencerProviderPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiSequencerProvider;
import org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortProviderImpl;
import org.herac.tuxguitar.player.impl.jsa.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiConfigUtils;

public class MidiPluginList extends TGPluginList implements TGPluginSetup{
	
	protected List getPlugins() {
		List plugins = new ArrayList();
		plugins.add(new TGMidiOutputPortProviderPlugin() {
			protected MidiOutputPortProvider getProvider() {
				return new MidiPortProviderImpl();
			}
		});
		plugins.add(new TGMidiSequencerProviderPlugin() {
			protected MidiSequencerProvider getProvider() {
				return new MidiSequencerProviderImpl();
			}
		});
		return plugins;
	}
	
	public void setupDialog(Shell parent) {
		MidiConfigUtils.setupDialog(parent);
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "Java Sound Api plugin";
	}
	
	public String getName() {
		return "Java Sound Api plugin";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
