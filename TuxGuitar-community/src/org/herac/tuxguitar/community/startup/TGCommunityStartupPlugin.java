package org.herac.tuxguitar.community.startup;

import org.herac.tuxguitar.community.TGCommunityPlugin;
import org.herac.tuxguitar.util.plugin.TGPlugin;

public class TGCommunityStartupPlugin implements TGPlugin {
	
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
	
	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}
}
