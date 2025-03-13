package app.tuxguitar.app.io.stream;

import app.tuxguitar.io.base.TGSongStreamContext;

public interface TGSongStreamSettingsHandler {

	String getProviderId();

	void handleSettings(TGSongStreamContext context, Runnable callback);
}
