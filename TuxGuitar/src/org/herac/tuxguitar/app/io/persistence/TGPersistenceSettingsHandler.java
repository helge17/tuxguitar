package org.herac.tuxguitar.app.io.persistence;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public interface TGPersistenceSettingsHandler {
	
	TGFileFormat getFileFormat();
	
	TGPersistenceSettingsMode getMode();
	
	void handleSettings(TGSongStreamContext context, Runnable callback);
}
