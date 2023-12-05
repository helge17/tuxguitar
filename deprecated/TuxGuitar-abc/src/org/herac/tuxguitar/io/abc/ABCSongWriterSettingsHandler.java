package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class ABCSongWriterSettingsHandler implements TGPersistenceSettingsHandler {

	private TGContext context;
	
	public ABCSongWriterSettingsHandler(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return ABCFileFormat.FILE_FORMAT;
	}

	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.WRITE;
	}
	
	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
				final ABCSettings settings = ABCSettings.getDefaults();
				
				new ABCSongWriterSettingsDialog(ABCSongWriterSettingsHandler.this.context, song).open(settings, new Runnable() {
					public void run() {
						if( settings != null ) {
							context.setAttribute(ABCSettings.class.getName(), settings);
							callback.run();
						}
					}
				});
			}
		});
	}	
}
