package org.herac.tuxguitar.cocoa.toolbar;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginAdapter;

public class MacToolbarPlugin extends TGPluginAdapter {
	
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
}
