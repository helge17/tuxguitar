package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP2InputStreamPlugin extends GTPInputStreamPlugin{

	public GP2InputStreamPlugin() {
		super();
	}
	
	protected TGInputStreamBase createInputStream() throws TGPluginException {
		return new GP2InputStream(GTPSettingsUtil.getInstance(getContext()).getSettings());
	}
}
