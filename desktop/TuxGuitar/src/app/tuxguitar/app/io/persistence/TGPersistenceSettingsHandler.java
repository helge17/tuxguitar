package app.tuxguitar.app.io.persistence;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;

public interface TGPersistenceSettingsHandler {

	TGFileFormat getFileFormat();

	TGPersistenceSettingsMode getMode();

	void handleSettings(TGSongStreamContext context, Runnable callback, TGPersistenceSettingsMode mode);
}
