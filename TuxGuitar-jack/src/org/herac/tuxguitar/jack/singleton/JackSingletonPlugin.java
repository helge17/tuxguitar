package org.herac.tuxguitar.jack.singleton;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

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
