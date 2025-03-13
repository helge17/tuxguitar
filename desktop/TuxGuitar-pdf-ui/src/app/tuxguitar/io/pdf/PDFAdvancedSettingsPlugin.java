package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import org.herac.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class PDFAdvancedSettingsPlugin extends TGPluginSettingsAdapter {

	@Override
	public String getModuleId() {
		return PDFSongWriterPlugin.MODULE_ID;
	}

	@Override
	protected TGPluginSettingsHandler createHandler(TGContext context) throws TGPluginException {
		return new PDFSettingsHandler(context);
	}

}
