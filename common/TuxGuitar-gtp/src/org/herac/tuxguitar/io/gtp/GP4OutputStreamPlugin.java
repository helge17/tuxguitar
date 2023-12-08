package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class GP4OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP4OutputStreamPlugin() {
		super();
	}
	
	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new GP4OutputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
