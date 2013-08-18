package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class ImageExporterPlugin extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-image";
	
	protected TGRawExporter getExporter() {
		return new ImageExporterDialog();
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
