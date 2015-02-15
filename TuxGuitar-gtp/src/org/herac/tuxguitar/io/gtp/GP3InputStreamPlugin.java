package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP3InputStreamPlugin extends GTPInputStreamPlugin{

	public GP3InputStreamPlugin() {
		super();
	}
	
	protected TGInputStreamBase createInputStream() throws TGPluginException {
		return new GP3InputStream(GTPSettingsUtil.getInstance(getContext()).getSettings());
	}
}
