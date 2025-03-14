package app.tuxguitar.io.gtp;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class GP4OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP4OutputStreamPlugin() {
		super();
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new GP4OutputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
