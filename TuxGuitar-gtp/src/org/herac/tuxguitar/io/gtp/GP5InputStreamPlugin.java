package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP5InputStreamPlugin extends GTPInputStreamPlugin{

	public GP5InputStreamPlugin() {
		super();
	}

	protected TGInputStreamBase createInputStream(TGContext context) throws TGPluginException {
		return new GP5InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
