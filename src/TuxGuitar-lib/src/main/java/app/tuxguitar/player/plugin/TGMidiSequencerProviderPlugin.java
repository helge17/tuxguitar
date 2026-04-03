package app.tuxguitar.player.plugin;

import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiSequencerProvider;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGMidiSequencerProviderPlugin implements TGPlugin{

	private MidiSequencerProvider provider;

	protected abstract MidiSequencerProvider createProvider(TGContext context) throws TGPluginException;

	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.provider == null ) {
				this.provider = createProvider(context);

				MidiPlayer.getInstance(context).addSequencerProvider(this.provider);
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.provider != null ) {
				MidiPlayer.getInstance(context).removeSequencerProvider(this.provider);

				this.provider.closeAll();
				this.provider = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
}
