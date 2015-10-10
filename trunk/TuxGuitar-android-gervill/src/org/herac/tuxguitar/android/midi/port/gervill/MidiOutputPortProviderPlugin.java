package org.herac.tuxguitar.android.midi.port.gervill;

import javax.sound.ServiceProvider;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin {
	
	private static final String MODULE_ID = "tuxguitar-android-gervill-midi";
	
	public MidiOutputPortProviderPlugin() {
		super();
	}
	
	@Override
	public void connect(TGContext context) throws TGPluginException {
		ServiceProvider.setContext(context);
		
		super.connect(context);
	}
	
	@Override
	public void disconnect(TGContext context) throws TGPluginException {
		super.disconnect(context);
		
		ServiceProvider.setContext(null);
	}
	
	protected MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl(context);
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
