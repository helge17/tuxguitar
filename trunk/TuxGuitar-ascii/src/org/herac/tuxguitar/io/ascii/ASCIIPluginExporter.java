package org.herac.tuxguitar.io.ascii;

import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class ASCIIPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new ASCIISongExporter();
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "ASCII tab exporter";
	}
	
	public String getName() {
		return "ASCII tab exporter";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
