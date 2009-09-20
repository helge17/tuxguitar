package org.herac.tuxguitar.cocoa.opendoc;

import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginAdapter;

public class OpenDocPlugin extends TGPluginAdapter {
	
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
}
