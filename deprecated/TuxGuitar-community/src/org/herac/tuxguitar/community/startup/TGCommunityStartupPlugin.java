package org.herac.tuxguitar.community.startup;

import org.herac.tuxguitar.community.TGCommunityPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGCommunityStartupPlugin implements TGPlugin {
	
	private boolean done;
	
	public TGCommunityStartupPlugin(){
		this.done = false;
	}
	
	public String getModuleId(){
		return TGCommunityPlugin.MODULE_ID;
	}

	public void connect(TGContext context) throws TGPluginException {
		if(!this.done){
			TGCommunityStartupScreen startup = new TGCommunityStartupScreen(context);
			if(!startup.isDisabled()){
				startup.open();
			}
		}
		this.done = true;
	}

	public void disconnect(TGContext context) throws TGPluginException {
		// Nothing to do.
	}
}
