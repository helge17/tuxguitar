package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsHandler;
import org.herac.tuxguitar.app.io.persistence.TGPersistenceSettingsMode;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

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
