package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public abstract class TGMidiOutputPortProviderPlugin extends TGPluginAdapter{
	private boolean loaded;
	private MidiOutputPortProvider provider;
	
	protected abstract MidiOutputPortProvider getProvider() throws TGPluginException;
	
	public void init() throws TGPluginException {
		this.provider = getProvider();
		this.loaded = false;
	}
	
	public void close() throws TGPluginException {
		try {
			this.provider.closeAll();
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
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
			try {
				TuxGuitar.instance().getPlayer().addOutputPortProvider(this.provider,TuxGuitar.instance().isInitialized());
				this.loaded = true;
			} catch (Throwable throwable) {
				throw new TGPluginException(throwable.getMessage(),throwable);
			}
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			try {
				TuxGuitar.instance().getPlayer().removeOutputPortProvider(this.provider);
				this.loaded = false;
			} catch (Throwable throwable) {
				throw new TGPluginException(throwable.getMessage(),throwable);
			}
		}
	}
}
