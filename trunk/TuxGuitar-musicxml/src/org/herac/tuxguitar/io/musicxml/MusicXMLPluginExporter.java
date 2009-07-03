package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class MusicXMLPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new MusicXMLSongExporter();
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "MusicXML exporter plugin";
	}
	
	public String getName() {
		return "MusicXML exporter";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
