package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandlerPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class ABCSongReaderSettingsHandlerPlugin extends TGPersistenceSettingsHandlerPlugin {

	public String getModuleId() {
		return ABCPlugin.MODULE_ID;
	}
	
	public TGPersistenceSettingsHandler createSettingsHandler(TGContext context) throws TGPluginException {
		return new ABCSongReaderSettingsHandler(context);
	}
}
