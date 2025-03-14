package app.tuxguitar.io.image;

import app.tuxguitar.app.io.stream.TGSongStreamSettingsHandler;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class ImageExporterSettingsHandler implements TGSongStreamSettingsHandler {

	private TGContext context;

	public ImageExporterSettingsHandler(TGContext context) {
		this.context = context;
	}

	public String getProviderId() {
		return ImageExporter.PROVIDER_ID;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				new ImageExporterSettingsDialog(ImageExporterSettingsHandler.this.context).openSettingsDialog(context, callback);
			}
		});
	}
}
