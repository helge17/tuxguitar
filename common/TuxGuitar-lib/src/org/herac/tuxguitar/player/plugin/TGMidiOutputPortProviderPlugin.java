package org.herac.tuxguitar.player.plugin;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGMidiOutputPortProviderPlugin implements TGPlugin{
	
	private MidiOutputPortProvider provider;
	
	protected abstract MidiOutputPortProvider createProvider(TGContext context) throws TGPluginException;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.provider == null ) {
				this.provider = createProvider(context);
				
				MidiPlayer.getInstance(context).addOutputPortProvider(this.provider);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.provider != null ) {
				MidiPlayer.getInstance(context).removeOutputPortProvider(this.provider);
				
				this.provider.closeAll();
				this.provider = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}
