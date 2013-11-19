package org.herac.tuxguitar.jack.connection;

import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.jack.singleton.JackClientInstanceProvider;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class JackConnectionPlugin implements TGPlugin{

	private JackConnectionManager jackConnectionManager;
	
	public void init() throws TGPluginException {
		this.jackConnectionManager = new JackConnectionManager(new JackClientInstanceProvider());
	}
	
	public void close() throws TGPluginException {
		this.jackConnectionManager = null;
	}

	public JackConnectionManager getJackConnectionManager() {
		return this.jackConnectionManager;
	}

	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}

	public void setEnabled(boolean enabled) throws TGPluginException {
		try {
			if( enabled ){
				this.jackConnectionManager.initialize();
			} else {
				this.jackConnectionManager.destroy();
			}
		} catch (Throwable throwable){
			throw new TGPluginException(throwable);
		}
	}
}
