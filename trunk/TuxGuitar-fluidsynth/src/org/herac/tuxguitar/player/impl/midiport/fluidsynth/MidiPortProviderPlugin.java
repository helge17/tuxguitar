package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiPortProvider;

public class MidiPortProviderPlugin extends TGMidiPortProviderPlugin implements TGPluginSetup{
	
	private MidiPortProviderImpl provider;
	
	protected MidiPortProvider getProvider() {
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
	
	private MidiPortProviderImpl getProviderImpl() {
		if(this.provider == null){
			this.provider = new MidiPortProviderImpl();
		}
		return this.provider;
	}
}
