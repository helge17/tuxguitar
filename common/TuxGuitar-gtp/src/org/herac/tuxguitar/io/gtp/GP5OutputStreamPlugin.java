package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP5OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP5OutputStreamPlugin() {
		super();
	}
	
	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new GP5OutputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
