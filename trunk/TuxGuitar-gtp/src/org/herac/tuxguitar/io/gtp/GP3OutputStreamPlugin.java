package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP3OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP3OutputStreamPlugin() {
		super();
	}
	
	protected TGOutputStreamBase createOutputStream() throws TGPluginException {
		return new GP3OutputStream(GTPSettingsUtil.getInstance(getContext()).getSettings());
	}
}
