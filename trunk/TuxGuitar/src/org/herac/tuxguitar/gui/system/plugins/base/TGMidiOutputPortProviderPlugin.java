package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiOutputPortProvider;

public abstract class TGMidiOutputPortProviderPlugin extends TGPluginAdapter{
	private boolean loaded;
	private MidiOutputPortProvider provider;
	
	protected abstract MidiOutputPortProvider getProvider();
	
	public void init() {
		this.provider = getProvider();
		this.loaded = false;
	}
	
	public void close()throws TGPluginException{
		try {
			this.provider.closeAll();
		} catch (MidiPlayerException e) {
			throw new TGPluginException(e.getMessage(),e);
		}
	}
	
	protected void addPlugin() throws TGPluginException{
		if(!this.loaded){
			try {
				TuxGuitar.instance().getPlayer().addOutputPortProvider(this.provider);
				this.loaded = true;
			} catch (MidiPlayerException e) {
				throw new TGPluginException(e.getMessage(),e);
			}
			
		}
	}
	
	protected void removePlugin() throws TGPluginException{
		if(this.loaded){
			try {
				TuxGuitar.instance().getPlayer().removeOutputPortProvider(this.provider);
				this.loaded = false;
			} catch (MidiPlayerException e) {
				throw new TGPluginException(e.getMessage(),e);
			}
		}
	}
	
	public void setEnabled(boolean enabled)throws TGPluginException {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}
}
