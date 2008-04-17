package org.herac.tuxguitar.player.impl.midiport.oss;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGMidiPortProviderPlugin;
import org.herac.tuxguitar.player.base.MidiPortProvider;

public class MidiPortProviderPlugin extends TGMidiPortProviderPlugin implements TGPluginSetup{

	private MidiPortProviderImpl portReader;
	
	protected MidiPortProvider getProvider() {
		if(this.portReader == null){
			this.portReader = new MidiPortProviderImpl();
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
		return "1.0-rc3";
	}

	public void setupDialog(Shell parent) {
		MidiConfigUtils.setupDialog(parent,(MidiPortProviderImpl)getProvider());
	}
	
}
