package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.community.TGCommunityPlugin;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGShareSongPlugin extends TGExporterPlugin {
	
	protected TGRawExporter createExporter(TGContext context) throws TGPluginException {
		return new TGShareSongExporter(context);
	}
	
	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}
}
