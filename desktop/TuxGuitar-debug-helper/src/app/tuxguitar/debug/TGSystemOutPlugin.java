package app.tuxguitar.debug;

import java.io.IOException;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSystemOutPlugin implements TGPlugin {

	public String getModuleId() {
		return TGDebugHelperPlugin.MODULE_ID;
	}

	public void connect(TGContext context) throws TGPluginException {
		try {
			TGSystemOutManager.getInstance(context).install();
		} catch (IOException e) {
			throw new TGPluginException(e);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			TGSystemOutManager.getInstance(context).uninstall();
		} catch (IOException e) {
			throw new TGPluginException(e);
		}
	}
}
