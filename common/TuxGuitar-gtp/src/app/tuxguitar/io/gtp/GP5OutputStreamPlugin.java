package app.tuxguitar.io.gtp;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class GP5OutputStreamPlugin extends GTPOutputStreamPlugin{

	public GP5OutputStreamPlugin() {
		super();
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new GP5OutputStream(GTPSettingsManager.getInstance(context).getSettings());
	}
}
