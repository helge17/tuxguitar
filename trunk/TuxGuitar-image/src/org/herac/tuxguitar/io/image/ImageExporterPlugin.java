package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class ImageExporterPlugin extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-image";
	
	protected TGRawExporter createExporter(TGContext context) {
		return new ImageExporter(context);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
