package org.herac.tuxguitar.player.impl.midiport.oss;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin implements TGPluginSetup{
	
	private MidiOutputPortProviderImpl portReader;
	
	protected MidiOutputPortProvider getProvider() {
		if(this.portReader == null){
			this.portReader = new MidiOutputPortProviderImpl();
		}
		return this.portReader;
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "OSS output plugin";
	}
	
	public String getName() {
		return "OSS output plugin";
	}
	
	public String getVersion() {
		return "1.0";
	}
	
	public void setupDialog(Shell parent) {
		MidiConfigUtils.setupDialog(parent,(MidiOutputPortProviderImpl)getProvider());
	}
}
