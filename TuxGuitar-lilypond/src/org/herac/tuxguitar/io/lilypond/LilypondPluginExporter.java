package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class LilypondPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-lilypond";
	
	protected TGRawExporter getExporter() {
		return new LilypondSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
