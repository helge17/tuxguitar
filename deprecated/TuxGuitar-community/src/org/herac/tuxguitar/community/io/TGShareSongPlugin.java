package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.community.TGCommunityPlugin;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.io.plugin.TGSongExporterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGShareSongPlugin extends TGSongExporterPlugin {
	
	protected TGSongExporter createExporter(TGContext context) throws TGPluginException {
		return new TGShareSongExporter(context);
	}
	
	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}
}
