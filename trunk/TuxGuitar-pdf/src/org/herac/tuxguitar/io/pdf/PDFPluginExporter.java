package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class PDFPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-pdf";
	
	protected TGRawExporter getExporter() {
		return new PDFSongExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
