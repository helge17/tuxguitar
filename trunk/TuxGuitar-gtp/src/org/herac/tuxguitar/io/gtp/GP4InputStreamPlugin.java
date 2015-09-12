package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP4InputStreamPlugin extends GTPInputStreamPlugin{

	public GP4InputStreamPlugin() {
		super();
	}
	
	protected TGInputStreamBase createInputStream(TGContext context) throws TGPluginException {
		return new GP4InputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
