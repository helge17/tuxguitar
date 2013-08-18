package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGExporterPluginImpl extends TGExporterPlugin{
	
	private TGRawExporter rawExporter;
	
	public TGExporterPluginImpl(TGRawExporter rawExporter){
		this.rawExporter = rawExporter;
	}
	
	protected TGRawExporter getExporter() throws TGPluginException {
		return this.rawExporter;
	}
	
	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
