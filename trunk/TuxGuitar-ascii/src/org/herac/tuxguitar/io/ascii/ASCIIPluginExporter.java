package org.herac.tuxguitar.io.ascii;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class ASCIIPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-ascii";
	
	protected TGRawExporter createExporter(TGContext context) {
		return new ASCIISongExporter();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
