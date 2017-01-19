package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
