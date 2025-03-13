package app.tuxguitar.io.pdf;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class PDFSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public PDFSettingsHandlerPlugin() {
		super();
	}

	public String getModuleId() {
		return PDFSongWriterPlugin.MODULE_ID;
	}

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new PDFSettingsHandler(context);
	}
}
