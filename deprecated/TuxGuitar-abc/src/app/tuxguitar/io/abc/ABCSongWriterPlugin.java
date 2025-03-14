package app.tuxguitar.io.abc;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.plugin.TGSongWriterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class ABCSongWriterPlugin extends TGSongWriterPlugin {

	public ABCSongWriterPlugin() {
		super(false);
	}

	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new ABCSongWriter();
	}
}
