package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class MidiToAudioPlugin extends TGExporterPlugin{
	
	private boolean available;
	
	public MidiToAudioPlugin(){
		this.available = MidiToAudioSynth.instance().isAvailable();
	}
	
	public void init() throws TGPluginException {
		if( this.available ){
			super.init();
		}
	}
	
	public void close() throws TGPluginException {
		if( this.available ){
			super.close();
		}
	}
	
	public void setEnabled( boolean enabled ) throws TGPluginException {
		if( this.available ){
			super.setEnabled( enabled );
		}
	}
	
	protected TGRawExporter getExporter() throws TGPluginException {
		if( this.available ){
			return new MidiToAudioExporter();
		}
		return null;
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
		description += ("See more about Gervill: https://gervill.dev.java.net/\n\n");
		description += ("This plugin will only work if gervill synthesizer is installed in your JVM");
		return description;
	}
}
