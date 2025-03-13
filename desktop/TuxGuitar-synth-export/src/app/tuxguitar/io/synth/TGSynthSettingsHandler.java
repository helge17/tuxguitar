package app.tuxguitar.io.synth;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class TGSynthSettingsHandler implements TGPersistenceSettingsHandler {

	private TGContext context;

	public TGSynthSettingsHandler(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return TGSynthSongWriter.FILE_FORMAT;
	}

	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.WRITE;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final TGSynthAudioSettings midiToAudioSettings = new TGSynthAudioSettings();

				new TGSynthSettingsDialog(TGSynthSettingsHandler.this.context).open(midiToAudioSettings, new Runnable() {
					public void run() {
						context.setAttribute(TGSynthAudioSettings.class.getName(), midiToAudioSettings);
						context.setAttribute(TGFileFormat.class.getName(), new TGSynthAudioFormat(midiToAudioSettings));
						callback.run();
					}
				});
			}
		});
	}
}
