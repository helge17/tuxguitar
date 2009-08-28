package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;

public class TGShareSongPlugin extends TGExporterPlugin {
	
	protected TGRawExporter getExporter() throws TGPluginException {
		return new TGShareSongExporter();
	}
}
