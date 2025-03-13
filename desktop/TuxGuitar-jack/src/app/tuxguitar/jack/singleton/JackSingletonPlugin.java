package app.tuxguitar.jack.singleton;

import app.tuxguitar.jack.JackClient;
import app.tuxguitar.jack.JackPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public class JackSingletonPlugin implements TGPlugin {

	public static final String MODULE_ID = "tuxguitar-jack";

	private JackClient jackClient;

	public JackSingletonPlugin(){
		super();
	}

	public JackClient getJackClient() {
		return this.jackClient;
	}

	public String getModuleId() {
		return JackPlugin.MODULE_ID;
	}

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.jackClient == null ){
				this.jackClient = new JackClient();
			}
		} catch (Throwable throwable){
			throw new TGPluginException(throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.jackClient != null ){
				if( this.jackClient.isOpen() ){
					this.jackClient.close();
				}
				this.jackClient.finalize();
				this.jackClient = null;
			}
		} catch (Throwable throwable){
			throw new TGPluginException(throwable);
		}
	}
}
