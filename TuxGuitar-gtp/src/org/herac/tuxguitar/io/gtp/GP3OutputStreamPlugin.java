package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP3OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP3OutputStreamPlugin() {
		super();
	}
	
	protected TGOutputStreamBase createOutputStream(TGContext context) throws TGPluginException {
		return new GP3OutputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
