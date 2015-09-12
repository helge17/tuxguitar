package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP2InputStreamPlugin extends GTPInputStreamPlugin{

	public GP2InputStreamPlugin() {
		super();
	}
	
	protected TGInputStreamBase createInputStream(TGContext context) throws TGPluginException {
		return new GP2InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
