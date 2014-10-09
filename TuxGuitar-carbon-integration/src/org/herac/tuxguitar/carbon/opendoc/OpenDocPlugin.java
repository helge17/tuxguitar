package org.herac.tuxguitar.carbon.opendoc;

import org.herac.tuxguitar.carbon.TGCarbonIntegrationPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class OpenDocPlugin implements TGPlugin {
	
	private OpenDocListener openDocListener;
	
	public void init(TGContext context) throws TGPluginException {
		// Nothing todo
	}
	
	public void close() throws TGPluginException {
		// Nothing todo
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		if( this.openDocListener != null ){
			this.openDocListener.setEnabled(enabled);
		}else if(enabled){
			this.openDocListener = new OpenDocListener();
			this.openDocListener.setEnabled(true);
			this.openDocListener.init();
		}
	}
	
	public String getModuleId() {
		return TGCarbonIntegrationPlugin.MODULE_ID;
	}
}
