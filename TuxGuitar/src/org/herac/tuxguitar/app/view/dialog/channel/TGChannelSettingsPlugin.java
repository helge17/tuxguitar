package org.herac.tuxguitar.app.view.dialog.channel;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGChannelSettingsPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGContext context;
	private TGChannelSettingsHandler handler;
	
	protected abstract TGChannelSettingsHandler getHandler() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
		this.handler = getHandler();
		this.loaded = false;
	}
	
	public TGContext getContext() {
		return this.context;
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
			TuxGuitar.getInstance().getChannelManager().getChannelSettingsHandlerManager().addChannelSettingsHandler(this.handler);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TuxGuitar.getInstance().getChannelManager().getChannelSettingsHandlerManager().removeChannelSettingsHandler(this.handler);
			this.loaded = false;
		}
	}
}
