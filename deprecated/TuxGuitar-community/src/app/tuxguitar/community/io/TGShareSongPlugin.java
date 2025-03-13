package app.tuxguitar.community.io;

import app.tuxguitar.community.TGCommunityPlugin;
import app.tuxguitar.io.base.TGSongExporter;
import app.tuxguitar.io.plugin.TGSongExporterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGShareSongPlugin extends TGSongExporterPlugin {

	protected TGSongExporter createExporter(TGContext context) throws TGPluginException {
		return new TGShareSongExporter(context);
	}

	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}
}
