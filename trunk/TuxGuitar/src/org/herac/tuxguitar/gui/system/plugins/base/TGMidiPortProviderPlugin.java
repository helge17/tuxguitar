package org.herac.tuxguitar.gui.system.plugins.base;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.plugins.TGPluginException;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiPortProvider;

public abstract class TGMidiPortProviderPlugin extends TGPluginAdapter{
	private boolean loaded;
	private MidiPortProvider provider;
	
	protected abstract MidiPortProvider getProvider();
	
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
				TuxGuitar.instance().getPlayer().addPortProvider(this.provider);
				this.loaded = true;
			} catch (MidiPlayerException e) {
				throw new TGPluginException(e.getMessage(),e);
			}
			
		}
	}
	
	protected void removePlugin() throws TGPluginException{
		if(this.loaded){
			try {
				TuxGuitar.instance().getPlayer().removePortProvider(this.provider);
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
