package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class ImageExporterPlugin extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new ImageExporterDialog();
	}
	
	public String getVersion() {
		return "0.1";
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getName() {
		return "Image exporter";
	}
	
	public String getDescription() {
		return "Image exporter";
	}
}
