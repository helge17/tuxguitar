package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.system.plugins.TGPluginException;
import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class SVGExporterPlugin extends TGExporterPlugin {
	
	protected TGRawExporter getExporter() throws TGPluginException {
		return new SVGExporter(new SVGExporterStylesDialog());
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "SVG exporter plugin";
	}
	
	public String getName() {
		return "SVG exporter";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
