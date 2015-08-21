package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class SVGExporterPlugin extends TGExporterPlugin {
	
	public static final String MODULE_ID = "tuxguitar-svg";
	
	public TGRawExporter createExporter(TGContext context) throws TGPluginException {
		return new SVGExporter();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
