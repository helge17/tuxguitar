package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class LilypondPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-lilypond";
	
	protected TGRawExporter getExporter() {
		return new LilypondSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
