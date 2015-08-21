package org.herac.tuxguitar.io.tg;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGExporterPluginImpl extends TGExporterPlugin{
	
	private TGRawExporter rawExporter;
	
	public TGExporterPluginImpl(TGRawExporter rawExporter){
		this.rawExporter = rawExporter;
	}
	
	protected TGRawExporter createExporter(TGContext context) throws TGPluginException {
		return this.rawExporter;
	}
	
	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
