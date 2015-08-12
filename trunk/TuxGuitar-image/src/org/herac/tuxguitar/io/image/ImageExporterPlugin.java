package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;

public class ImageExporterPlugin extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-image";
	
	protected TGRawExporter getExporter() {
		return new ImageExporter(getContext());
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
