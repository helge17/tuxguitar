package org.herac.tuxguitar.app.io;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGSongStreamSettingsHandlerPlugin implements TGPlugin {
	
	private TGContext context;
	private TGSongStreamSettingsHandler handler;
	
	protected abstract TGSongStreamSettingsHandler getSettingsHandler() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
	}
	
	public TGContext getContext() {
		return this.context;
	}

	public void close() throws TGPluginException {
		this.removePlugin();
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		if( enabled ){
			this.addPlugin();
		}else{
			this.removePlugin();
		}
	}
	
	protected void addPlugin() throws TGPluginException {
		if( this.handler == null ){
			this.handler = this.getSettingsHandler();
			if( this.handler != null ){
				TGSongStreamAdapterManager.getInstance(this.context).addSettingsHandler(this.handler);
			}
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if( this.handler != null ){
			TGSongStreamAdapterManager.getInstance(this.context).removeSettingsHandler(this.handler);
			this.handler = null;
		}
	}
}
