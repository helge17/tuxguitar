package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class MusicXMLPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-musicxml";
	
	protected TGRawExporter createExporter(TGContext context) {
		return new MusicXMLSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
