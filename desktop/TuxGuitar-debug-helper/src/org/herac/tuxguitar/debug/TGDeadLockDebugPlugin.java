package org.herac.tuxguitar.debug;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
