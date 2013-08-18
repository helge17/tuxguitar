package org.herac.tuxguitar.app.system.plugins.base;

import org.herac.tuxguitar.app.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGBrowserPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGBrowserFactory factory;
	
	protected abstract TGBrowserFactory getFactory() throws TGPluginException;
	
	public void init() throws TGPluginException {
		this.factory = getFactory();
		this.loaded = false;
	}
	
	public void close() throws TGPluginException {
		this.loaded = false;
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}
	
	protected void addPlugin() throws TGPluginException {
		if(!this.loaded){
			TGBrowserManager.instance().addFactory(this.factory);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGBrowserManager.instance().removeFactory(this.factory);
			this.loaded = false;
		}
	}
}
