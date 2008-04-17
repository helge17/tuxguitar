package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.tools.browser.TGBrowserManager;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;

public abstract class TGBrowserPlugin extends TGPluginAdapter{
	private boolean loaded;
	private TGBrowserFactory factory;

	protected abstract TGBrowserFactory getFactory();
	
	public void init() {
		this.factory = getFactory();
		this.loaded = false;
	}

	public void close() {
		this.loaded = false;
	}

	protected void addPlugin(){
		if(!this.loaded){
			TGBrowserManager.instance().addFactory(this.factory);
			this.loaded = true;
		}
	}
	
	protected void removePlugin(){		
		if(this.loaded){
			TGBrowserManager.instance().removeFactory(this.factory);
			this.loaded = false;
		}
	}	

	public void setEnabled(boolean enabled) {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}	
}
