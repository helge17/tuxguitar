package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP4OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP4OutputStreamPlugin() {
		super();
	}
	
	protected TGOutputStreamBase createOutputStream() throws TGPluginException {
		return new GP4OutputStream(GTPSettingsUtil.getInstance(getContext()).getSettings());
	}
}
