package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.app.system.plugins.base.TGExporterPlugin;
import org.herac.tuxguitar.community.TGCommunityPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGShareSongPlugin extends TGExporterPlugin {
	
	protected TGRawExporter getExporter() throws TGPluginException {
		return new TGShareSongExporter();
	}
	
	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}
}
