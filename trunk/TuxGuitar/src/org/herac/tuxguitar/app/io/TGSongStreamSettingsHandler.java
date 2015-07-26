package org.herac.tuxguitar.app.io;

import org.herac.tuxguitar.io.base.TGSongStreamContext;

public interface TGSongStreamSettingsHandler {
	
	String getProviderId();
	
	void handleSettings(TGSongStreamContext context, Runnable callback);
}
