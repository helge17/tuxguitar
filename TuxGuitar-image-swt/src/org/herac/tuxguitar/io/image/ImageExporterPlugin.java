package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.io.plugin.TGSongExporterPlugin;
import org.herac.tuxguitar.util.TGContext;

public class ImageExporterPlugin extends TGSongExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-image";
	
	protected TGSongExporter createExporter(TGContext context) {
		return new ImageExporter(context);
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
