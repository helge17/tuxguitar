package app.tuxguitar.debug;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGDeadLockDebugPlugin implements TGPlugin {

	public String getModuleId() {
		return TGDebugHelperPlugin.MODULE_ID;
	}

	public void connect(TGContext context) throws TGPluginException {
		TGDeadLockDebugThread.getInstance(context).start();
	}

	public void disconnect(TGContext context) throws TGPluginException {
		TGDeadLockDebugThread.getInstance(context).stop();
	}
}
