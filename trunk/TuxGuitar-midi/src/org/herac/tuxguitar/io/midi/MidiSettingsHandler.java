package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MidiSettingsHandler implements TGSongStreamSettingsHandler {
	
	private TGContext context;
	
	public MidiSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public String getProviderId() {
		return MidiSongProvider.PROVIDER_ID;
	}
	
	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				MidiSettings settings = new MidiSettingsDialog().open();
				if( settings != null ) {
					context.setAttribute(MidiSettings.class.getName(), settings);
					callback.run();
				}
			}
		});
	}
}
