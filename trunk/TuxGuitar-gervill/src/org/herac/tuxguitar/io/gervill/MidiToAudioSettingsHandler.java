package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

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
		final MidiToAudioSettings midiToAudioSettings = new MidiToAudioSettings();
		
		new MidiToAudioSettingsDialog(this.context).open(midiToAudioSettings, new Runnable() {
			public void run() {
				context.setAttribute(MidiToAudioSettings.class.getName(), midiToAudioSettings);
				context.setAttribute(TGFileFormat.class.getName(), new MidiToAudioFormat(midiToAudioSettings));
				callback.run();
			}
		});
	}
}
