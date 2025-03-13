package app.tuxguitar.io.pdf;

import app.tuxguitar.app.system.plugins.TGPluginSettingsAdapter;
import app.tuxguitar.app.system.plugins.TGPluginSettingsHandler;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

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
