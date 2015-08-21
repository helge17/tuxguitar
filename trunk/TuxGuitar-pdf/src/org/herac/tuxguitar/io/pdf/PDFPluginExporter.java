package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class PDFPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-pdf";
	
	protected TGRawExporter createExporter(TGContext context) {
		return new PDFSongExporter(context);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
