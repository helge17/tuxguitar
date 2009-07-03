package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class MidiToAudioPlugin extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() throws TGPluginException {
		return new MidiToAudioExporter();
	}
	
	public void setEnabled( boolean enabled ) throws TGPluginException {
		if( enabled && !MidiToAudioSynth.instance().isAvailable() ){
			throw new TGPluginException("Gervill Synthesizer is not available");
		}
		super.setEnabled( enabled );
	}
	
	public String getVersion() {
		return "1.1";
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getName() {
		return "Gervill Plugin";
	}
	
	public String getDescription() {
		String description = new String();
		description += ("The purpose of this plugin is to add gervill support to tuxguitar.\n");
		description += ("The current version of this plugin includes \"Export to Audio\" feature.\n");
		description += ("See more about Gervill: https://gervill.dev.java.net/");
		return description;
	}
}
