package org.herac.tuxguitar.jack.connection;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackClientInstanceProvider;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class JackConnectionPlugin implements TGPlugin{

	private JackConnectionManager jackConnectionManager;
	
	public JackConnectionManager getJackConnectionManager() {
		return this.jackConnectionManager;
	}

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.jackConnectionManager == null ) {
				this.jackConnectionManager = new JackConnectionManager(context, new JackClientInstanceProvider(context));
				this.jackConnectionManager.initialize();
			}
		} catch (Throwable throwable){
			throw new TGPluginException(throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.jackConnectionManager != null ) {
				this.jackConnectionManager.destroy();
				this.jackConnectionManager = null;
			}
		} catch (Throwable throwable){
			throw new TGPluginException(throwable);
		}
	}

	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}
}
