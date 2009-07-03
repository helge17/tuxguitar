package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class PDFPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new PDFSongExporter();
	}
	
	public String getVersion() {
		return "1.0";
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getName() {
		return "PDF file format exporter";
	}
	
	public String getDescription() {
		String description = new String();
		description += ("TuxGuitar-pdf is an \"iText\" based *.pdf exporter plugin.");
		description += ("\niText Homepage: http://www.lowagie.com/iText/index.html");
		return description;
	}
}
