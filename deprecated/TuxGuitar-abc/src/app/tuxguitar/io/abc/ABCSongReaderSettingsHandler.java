package app.tuxguitar.io.abc;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class ABCSongReaderSettingsHandler implements TGPersistenceSettingsHandler {

	private TGContext context;

	public ABCSongReaderSettingsHandler(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return ABCFileFormat.FILE_FORMAT;
	}

	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.READ;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final ABCSettings settings = ABCSettings.getDefaults();

				new ABCSongReaderSettingsDialog(ABCSongReaderSettingsHandler.this.context).open(settings, new Runnable() {
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
