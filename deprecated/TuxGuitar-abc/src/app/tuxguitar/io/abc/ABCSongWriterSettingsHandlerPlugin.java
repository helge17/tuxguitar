package app.tuxguitar.io.abc;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class ABCSongWriterSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}

	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new ABCSongWriterSettingsHandler(context);
	}
}
