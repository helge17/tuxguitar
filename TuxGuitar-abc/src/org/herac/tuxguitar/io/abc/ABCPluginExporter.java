package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class ABCPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new ABCSongExporter();
	}
	
	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
}
