package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class MusicXMLPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-musicxml";
	
	protected TGRawExporter getExporter() {
		return new MusicXMLSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
