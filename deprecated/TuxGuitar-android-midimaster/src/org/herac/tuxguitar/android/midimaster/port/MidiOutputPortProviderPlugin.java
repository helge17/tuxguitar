package org.herac.tuxguitar.android.midimaster.port;

import org.herac.tuxguitar.player.base.MidiOutputPortProvider;
import org.herac.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiOutputPortProviderPlugin extends TGMidiOutputPortProviderPlugin{
	
	private static final String MODULE_ID = "tuxguitar-android-midimaster";

	public MidiOutputPortProviderPlugin() {
		super();
	}

	public void connect(TGContext context) throws TGPluginException {
		if( android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M ) {
			super.connect(context);
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		if( android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.M ) {
			super.disconnect(context);
		}
	}

	public MidiOutputPortProvider createProvider(TGContext context) {
		return new MidiOutputPortProviderImpl();
	}
	
	public String getModuleId() {
		return MODULE_ID;
	}
}
