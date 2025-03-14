package app.tuxguitar.community.startup;

import app.tuxguitar.community.TGCommunityPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

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
