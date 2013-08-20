package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class MusicXMLPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-musicxml";
	
	protected TGRawExporter getExporter() {
		return new MusicXMLSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
