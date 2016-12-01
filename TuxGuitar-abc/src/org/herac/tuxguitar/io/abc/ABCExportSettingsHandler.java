package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class ABCExportSettingsHandler implements TGSongStreamSettingsHandler {

	private TGContext context;
	
	public ABCExportSettingsHandler(TGContext context) {
		this.context = context;
	}
	
	public String getProviderId() {
		return ABCSongExporter.PROVIDER_ID;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
				final ABCSettings settings = ABCSettings.getDefaults();
				
				new ABCExportSettingsDialog(ABCExportSettingsHandler.this.context, song).open(settings, new Runnable() {
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
