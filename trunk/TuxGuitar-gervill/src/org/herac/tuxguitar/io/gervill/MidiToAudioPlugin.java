package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGSongExporter;

public class MidiToAudioPlugin extends TGExporterPlugin{
	
	protected TGSongExporter getExporter() throws TGPluginException {
		if( !MidiToAudioSynth.instance().isAvailable() ){
			throw new TGPluginException("Gervill Synthesizer is not available");
		}
		return new MidiToAudioExporter();
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
