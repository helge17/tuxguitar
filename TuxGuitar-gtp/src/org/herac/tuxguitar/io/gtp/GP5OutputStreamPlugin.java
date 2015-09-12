package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGOutputStreamBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP5OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP5OutputStreamPlugin() {
		super();
	}
	
	protected TGOutputStreamBase createOutputStream(TGContext context) throws TGPluginException {
		return new GP5OutputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
