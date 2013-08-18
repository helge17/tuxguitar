package org.herac.tuxguitar.community;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.community.browser.TGBrowserPluginImpl;
import org.herac.tuxguitar.community.io.TGShareSongPlugin;
import org.herac.tuxguitar.community.startup.TGCommunityStartupPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGCommunityPluginImpl extends TGPluginList {

	public static final String MODULE_ID = "tuxguitar-community";
	
	protected List getPlugins() throws TGPluginException {
		List plugins = new ArrayList();
		
		plugins.add(new TGShareSongPlugin());
		plugins.add(new TGBrowserPluginImpl());
		plugins.add(new TGCommunityStartupPlugin());
		
		return plugins;
	}
	
	public void init() throws TGPluginException{
		TGCommunitySingleton.getInstance().loadSettings();
		super.init();
	}
	
	public void close() throws TGPluginException{
		TGCommunitySingleton.getInstance().saveSettings();
		super.close();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
