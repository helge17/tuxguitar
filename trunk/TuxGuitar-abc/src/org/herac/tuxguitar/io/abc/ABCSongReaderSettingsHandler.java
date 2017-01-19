package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

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
