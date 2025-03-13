package app.tuxguitar.io.svg;

import app.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import app.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

public class SVGStylesHandler implements TGPersistenceSettingsHandler {

	private TGContext context;

	public SVGStylesHandler(TGContext context){
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return SVGSongWriter.FILE_FORMAT;
	}

	public TGPersistenceSettingsMode getMode() {
		return TGPersistenceSettingsMode.WRITE;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				final SVGStylesDialog styles = new SVGStylesDialog(SVGStylesHandler.this.context);

				styles.configure(new Runnable() {
					public void run() {
						context.setAttribute(SVGStyles.class.getName(), styles);
						callback.run();
					}
				});
			}
		});
	}
}
