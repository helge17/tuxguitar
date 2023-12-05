package org.herac.tuxguitar.io.midi;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MidiSettingsHandler implements TGPersistenceSettingsHandler {
	
	private TGContext context;
	
	public MidiSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public TGFileFormat getFileFormat() {
		return MidiFileFormat.FILE_FORMAT;
	}
	
	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.READ_WRITE;
	}
	
	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final MidiSettings settings = new MidiSettings();
				
				new MidiSettingsDialog(MidiSettingsHandler.this.context).open(settings, new Runnable() {
					public void run() {
						if( settings != null ) {
							context.setAttribute(MidiSettings.class.getName(), settings);
							callback.run();
						}
					}
				});
			}
		});
	}
}
