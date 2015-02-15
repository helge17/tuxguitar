package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP4InputStreamPlugin extends GTPInputStreamPlugin{

	public GP4InputStreamPlugin() {
		super();
	}
	
	protected TGInputStreamBase createInputStream() throws TGPluginException {
		return new GP4InputStream(GTPSettingsUtil.getInstance(getContext()).getSettings());
	}
}
