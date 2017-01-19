package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.plugin.TGSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
