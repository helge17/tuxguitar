package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class ABCPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new ABCSongExporter();
	}
	
	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
}
