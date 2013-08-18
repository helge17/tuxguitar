package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.cocoa.TGCocoaIntegrationPlugin;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MacToolbarPlugin implements TGPlugin {
	
	private MacToolbar macToolbar;
	
	public void init() throws TGPluginException {
		// Nothing todo
	}
	
	public void close() throws TGPluginException {
		try {
			if( this.macToolbar != null ){
				this.macToolbar.finalize();
			}
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		try {
			if( this.macToolbar != null ){
				this.macToolbar.setEnabled(enabled);
			}else if(enabled){
				this.macToolbar = new MacToolbar();
				this.macToolbar.setEnabled(true);
				this.macToolbar.init( TuxGuitar.instance().getShell() );
			}
		} catch( Throwable throwable ){
			throw new TGPluginException( throwable );
		}
	}
	
	public String getModuleId() {
		return TGCocoaIntegrationPlugin.MODULE_ID;
	}
}
