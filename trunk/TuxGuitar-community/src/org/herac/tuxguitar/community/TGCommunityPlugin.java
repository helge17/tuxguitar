package org.herac.tuxguitar.community;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGCommunityPlugin implements TGPlugin {

	public static final String MODULE_ID = "tuxguitar-community";
	
	private TGContext context;
	
	public void init(TGContext context) throws TGPluginException{
		this.context = context;
		
		TGCommunitySingleton.getInstance(this.context).loadSettings();
	}
	
	public void close() throws TGPluginException{
		TGCommunitySingleton.getInstance(this.context).saveSettings();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}

	public void setEnabled(boolean enabled) throws TGPluginException {
		// Not implemented.
	}
}
