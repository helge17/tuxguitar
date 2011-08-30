package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class ABCPluginExporter extends TGExporterPlugin{
	
	protected TGRawExporter getExporter() {
		return new ABCSongExporter();
	}
	
	public String getAuthor() {
		return "Peter Grootswagers <pgrootswagers@planet.nl>";
	}
	
	public String getDescription() {
		return "ABC 2.10.5 exporter plugin";
	}
	
	public String getName() {
		return "ABC exporter";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
