package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.app.io.TGSongStreamSettingsHandler;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class SVGExporterStylesHandler implements TGSongStreamSettingsHandler {

	public String getProviderId() {
		return SVGExporter.PROVIDER_ID;
	}

	public void handleSettings(TGSongStreamContext context, Runnable callback) {
		SVGExporterStyles styles = new SVGExporterStylesDialog();
		styles.configure();
		if( styles.isConfigured() ) {
			context.setAttribute(SVGExporterStyles.class.getName(), styles);
			callback.run();
		}
	}	
}
