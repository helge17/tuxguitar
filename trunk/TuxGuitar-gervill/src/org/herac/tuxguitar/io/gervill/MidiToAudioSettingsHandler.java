package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.io.base.TGSongStreamProvider;
import org.herac.tuxguitar.util.TGContext;

public class MidiToAudioSettingsHandler implements TGSongStreamSettingsHandler {

	private TGContext context;
	
	public MidiToAudioSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public String getProviderId() {
		return MidiToAudioExporter.PROVIDER_ID;
	}

	public void handleSettings(TGSongStreamContext context, Runnable callback) {
		MidiToAudioExporter exporter = context.getAttribute(TGSongStreamProvider.class.getName());
		exporter.getSettings().setDefaults();
		
		if( new MidiToAudioSettingsDialog(this.context).open( exporter.getSettings() ) ) {
			context.setAttribute(MidiToAudioSettings.class.getName(), exporter.getSettings());
			callback.run();
		}
	}
}
