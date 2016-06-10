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

	public void handleSettings(TGSongStreamContext context, Runnable callback) {
		SVGExporterStyles styles = new SVGExporterStylesDialog(this.context);
		styles.configure();
		if( styles.isConfigured() ) {
			context.setAttribute(SVGExporterStyles.class.getName(), styles);
			callback.run();
		}
	}	
}
