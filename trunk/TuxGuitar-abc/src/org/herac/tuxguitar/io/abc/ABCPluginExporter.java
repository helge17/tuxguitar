package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class ABCPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter createExporter(TGContext context) {
		return new ABCSongExporter();
	}
	
	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
}
