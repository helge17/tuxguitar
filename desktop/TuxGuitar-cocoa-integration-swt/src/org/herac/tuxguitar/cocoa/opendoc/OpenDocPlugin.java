package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class OpenDocPlugin implements TGPlugin {
	
	private OpenDocListener openDocListener;
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		try {
			if( this.openDocListener != null ){
				this.openDocListener.setEnabled(enabled);
			}else if(enabled){
				this.openDocListener = new OpenDocListener();
				this.openDocListener.setEnabled(true);
				this.openDocListener.init();
			}
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}
	
	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}
	
	public void connect(TGContext context) throws TGPluginException {
		this.setEnabled(true);
	}

	public void disconnect(TGContext context) throws TGPluginException {
		this.setEnabled(false);
	}
}
