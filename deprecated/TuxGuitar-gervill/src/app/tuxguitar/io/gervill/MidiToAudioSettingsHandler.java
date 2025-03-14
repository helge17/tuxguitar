package app.tuxguitar.io.gervill;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

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
