package org.herac.tuxguitar.app.editors.channel;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGChannelSettingsPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGChannelSettingsHandler handler;
	
	protected abstract TGChannelSettingsHandler getHandler() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
		this.handler = getHandler();
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
			TuxGuitar.instance().getChannelManager().getChannelSettingsHandlerManager().addChannelSettingsHandler(this.handler);
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TuxGuitar.instance().getChannelManager().getChannelSettingsHandlerManager().removeChannelSettingsHandler(this.handler);
			this.loaded = false;
		}
	}
}
