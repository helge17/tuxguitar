package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class OpenDocPlugin implements TGPlugin {
	
	private OpenDocListener openDocListener;
	
	public void init() throws TGPluginException {
		// Nothing todo
	}
	
	public void close() throws TGPluginException {
		// Nothing todo
	}
	
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
}
