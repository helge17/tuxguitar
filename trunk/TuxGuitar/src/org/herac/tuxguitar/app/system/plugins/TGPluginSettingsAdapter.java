package org.herac.tuxguitar.app.system.plugins;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGPluginSettingsAdapter implements TGPlugin{
	
	private boolean loaded;
	private TGContext context;
	private TGPluginSettingsHandler handler;
	
	protected abstract TGPluginSettingsHandler getHandler() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
		this.handler = getHandler();
		this.loaded = false;
	}
	
	public void close() throws TGPluginException {
		this.loaded = false;
	}
	
	public TGContext getContext() {
		return this.context;
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
			TGPluginSettingsManager.getInstance().addPluginSettingsHandler(getModuleId(), this.handler);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGPluginSettingsManager.getInstance().removePluginSettingsHandler(getModuleId());
			this.loaded = false;
		}
	}
}
