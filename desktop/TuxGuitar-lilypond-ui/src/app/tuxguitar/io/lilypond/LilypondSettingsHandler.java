package app.tuxguitar.io.lilypond;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class LilypondSettingsHandler implements TGPersistenceSettingsHandler {

	private TGContext context;

	public LilypondSettingsHandler(TGContext context)  {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return LilypondSongWriter.FILE_FORMAT;
	}

	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.WRITE;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback, TGPersistenceSettingsMode mode) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
				final LilypondSettings settings = LilypondSettings.getDefaults();

				new LilypondSettingsDialog(LilypondSettingsHandler.this.context, song).open(settings, new Runnable() {
					public void run() {
						if( settings != null ) {
							context.setAttribute(LilypondSettings.class.getName(), settings);
							callback.run();
						}
					}
				});
			}
		});
	}
}
