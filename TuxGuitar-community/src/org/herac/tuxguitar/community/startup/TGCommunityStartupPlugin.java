package org.herac.tuxguitar.community.startup;

import org.herac.tuxguitar.gui.system.plugins.base.TGPluginAdapter;

public class TGCommunityStartupPlugin extends TGPluginAdapter {
	
	private boolean done;
	
	public TGCommunityStartupPlugin(){
		this.done = false;
	}
	
	public void init() {
		// Nothing to init.
	}
	
	public void close() {
		// Nothing to close.
	}
	
	public void setEnabled(boolean enabled) {
		if(!this.done && enabled){
			TGCommunityStartupScreen startup = new TGCommunityStartupScreen();
			if( !startup.isDisabled() ){
				startup.open();
			}
		}
		this.done = true;
	}
}
