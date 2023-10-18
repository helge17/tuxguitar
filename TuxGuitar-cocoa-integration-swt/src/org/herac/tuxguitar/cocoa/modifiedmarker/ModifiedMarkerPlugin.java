package org.herac.tuxguitar.cocoa.modifiedmarker;

import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class ModifiedMarkerPlugin implements TGPlugin {
	
	private ModifiedMarker modifiedMarker;
	
	public void setEnabled(TGContext context, boolean enabled) throws TGPluginException {
		try {
			if( this.modifiedMarker != null ){
				this.modifiedMarker.setEnabled(enabled);
			}else if(enabled){
				this.modifiedMarker = new ModifiedMarker(context);
				this.modifiedMarker.setEnabled(true);
				this.modifiedMarker.init();
			}
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}
	
	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}
	
	public void connect(TGContext context) throws TGPluginException {
		this.setEnabled(context, true);
	}

	public void disconnect(TGContext context) throws TGPluginException {
		this.setEnabled(context, false);
	}
}
