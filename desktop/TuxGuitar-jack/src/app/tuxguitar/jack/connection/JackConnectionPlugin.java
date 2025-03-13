package app.tuxguitar.jack.connection;

import app.tuxguitar.jack.JackPlugin;
import app.tuxguitar.jack.singleton.JackClientInstanceProvider;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

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
