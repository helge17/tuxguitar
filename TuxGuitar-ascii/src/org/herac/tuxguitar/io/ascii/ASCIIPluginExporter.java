package org.herac.tuxguitar.io.ascii;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class ASCIIPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-ascii";
	
	protected TGRawExporter getExporter() {
		return new ASCIISongExporter();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
