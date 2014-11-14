package org.herac.tuxguitar.player.plugin;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGMidiOutputPortProviderPlugin implements TGPlugin{
	
	private boolean loaded;
	private MidiOutputPortProvider provider;
	private TGContext context;
	
	protected abstract MidiOutputPortProvider getProvider() throws TGPluginException;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
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
	
	public TGContext getContext() {
		return context;
	}

	protected void addPlugin() throws TGPluginException {
		if(!this.loaded){
			try {
				MidiPlayer.getInstance(getContext()).addOutputPortProvider(this.provider);
				this.loaded = true;
			} catch (Throwable throwable) {
				throw new TGPluginException(throwable.getMessage(),throwable);
			}
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			try {
				MidiPlayer.getInstance(getContext()).removeOutputPortProvider(this.provider);
				this.loaded = false;
			} catch (Throwable throwable) {
				throw new TGPluginException(throwable.getMessage(),throwable);
			}
		}
	}
}
