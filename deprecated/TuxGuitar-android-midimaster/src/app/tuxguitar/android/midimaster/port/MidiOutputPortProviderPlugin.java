package app.tuxguitar.android.midimaster.port;

import app.tuxguitar.player.base.MidiOutputPortProvider;
import app.tuxguitar.player.plugin.TGMidiOutputPortProviderPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

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
