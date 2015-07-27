package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class PDFPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-pdf";
	
	protected TGRawExporter getExporter() {
		return new PDFSongExporter(this.getContext());
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
