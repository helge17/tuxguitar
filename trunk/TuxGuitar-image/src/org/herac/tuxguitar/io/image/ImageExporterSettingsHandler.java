package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class ImageExporterSettingsHandler implements TGSongStreamSettingsHandler {
	
	public String getProviderId() {
		return ImageExporter.PROVIDER_ID;
	}
	
	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.instance().executeLater(new Runnable() {
			public void run() {
				new ImageExporterSettingsDialog().openSettingsDialog(context, callback);
			}
		});
	}
}
