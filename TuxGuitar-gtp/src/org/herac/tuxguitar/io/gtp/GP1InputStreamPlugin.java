package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP1InputStreamPlugin extends GTPInputStreamPlugin{

	public GP1InputStreamPlugin() {
		super();
	}
	
	protected TGInputStreamBase createInputStream(TGContext context) throws TGPluginException {
		return new GP1InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
