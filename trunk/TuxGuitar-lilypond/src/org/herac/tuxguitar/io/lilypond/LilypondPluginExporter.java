package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class LilypondPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-lilypond";
	
	protected TGRawExporter createExporter(TGContext context) {
		return new LilypondSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
