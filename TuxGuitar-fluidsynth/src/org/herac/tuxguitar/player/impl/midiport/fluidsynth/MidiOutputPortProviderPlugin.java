package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin implements TGPluginSetup{
	
	private MidiOutputPortProviderImpl provider;
	
	protected MidiOutputPortProvider getProvider() {
		return getProviderImpl();
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "FluidSynth output plugin";
	}
	
	public String getName() {
		return "FluidSynth output plugin";
	}
	
	public String getVersion() {
		return "1.0";
	}
	
	public void setupDialog(Shell parent) {
		getProviderImpl().getSettings().configure(parent);
	}
	
	private MidiOutputPortProviderImpl getProviderImpl() {
		if(this.provider == null){
			this.provider = new MidiOutputPortProviderImpl();
		}
		return this.provider;
	}
}
