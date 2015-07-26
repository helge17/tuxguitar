package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;

public class LilypondSettingsHandler implements TGSongStreamSettingsHandler {

	public String getProviderId() {
		return LilypondSongExporter.PROVIDER_ID;
	}

	public void handleSettings(TGSongStreamContext context, Runnable callback) {
		TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		LilypondSettings settings = new LilypondSettingsDialog(song).open();
		if( settings != null ) {
			context.setAttribute(LilypondSettings.class.getName(), settings);
			callback.run();
		}
	}	
}
