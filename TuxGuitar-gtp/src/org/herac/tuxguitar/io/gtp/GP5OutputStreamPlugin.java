package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP5OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP5OutputStreamPlugin() {
		super();
	}
	
	protected TGOutputStreamBase createOutputStream() throws TGPluginException {
		return new GP5OutputStream(GTPSettingsUtil.getInstance(getContext()).getSettings());
	}
}
