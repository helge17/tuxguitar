package org.herac.tuxguitar.io.ascii;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class ASCIIPluginExporter extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-ascii";
	
	protected TGRawExporter getExporter() {
		return new ASCIISongExporter();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
