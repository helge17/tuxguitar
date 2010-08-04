package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.app.system.plugins.TGPluginException;
import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class TGShareSongPlugin extends TGExporterPlugin {
	
	protected TGRawExporter getExporter() throws TGPluginException {
		return new TGShareSongExporter();
	}
}
