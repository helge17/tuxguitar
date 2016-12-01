package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class SVGExporterStylesHandler implements TGSongStreamSettingsHandler {
	
	private TGContext context;
	
	public SVGExporterStylesHandler(TGContext context){
		this.context = context;
	}
	
	public String getProviderId() {
		return SVGExporter.PROVIDER_ID;
	}

	public void handleSettings(final TGSongStreamContext context, final Runnable callback) {
		final SVGExporterStylesDialog styles = new SVGExporterStylesDialog(this.context);
		
		styles.configure(new Runnable() {
			public void run() {
				context.setAttribute(SVGExporterStyles.class.getName(), styles);
				callback.run();
			}
		});
	}	
}
