package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MidiToAudioSettingsHandler implements TGPersistenceSettingsHandler {

	private TGContext context;
	
	public MidiToAudioSettingsHandler(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return MidiToAudioSongWriter.FILE_FORMAT;
	}

	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.WRITE;
	}
	
	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final MidiToAudioSettings midiToAudioSettings = new MidiToAudioSettings();
				
				new MidiToAudioSettingsDialog(MidiToAudioSettingsHandler.this.context).open(midiToAudioSettings, new Runnable() {
					public void run() {
						context.setAttribute(MidiToAudioSettings.class.getName(), midiToAudioSettings);
						context.setAttribute(TGFileFormat.class.getName(), new MidiToAudioFormat(midiToAudioSettings));
						callback.run();
					}
				});
			}
		});
	}
}
