package org.herac.tuxguitar.community;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.community.browser.TGBrowserPluginImpl;
import org.herac.tuxguitar.community.io.TGShareSongPlugin;
import org.herac.tuxguitar.community.startup.TGCommunityStartupPlugin;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;

public class TGCommunityPluginImpl extends TGPluginList {

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
	
	public String getName() {
		return "TuxGuitar Community Integration";
	}
	
	public String getDescription() {
		return "TuxGuitar Community Integration";
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getVersion() {
		return "1.2";
	}
}
