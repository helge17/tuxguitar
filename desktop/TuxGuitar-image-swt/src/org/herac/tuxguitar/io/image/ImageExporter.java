package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class ImageExporter implements TGSongExporter{
	
	public static final String PROVIDER_ID = ImageExporter.class.getName();
	
	private TGContext context;
	
	public ImageExporter(TGContext context) {
		this.context = context;
	}
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
	
	public String getExportName() {
		return TuxGuitar.getProperty("tuxguitar-image.export-label");
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new ImageExporterStream(this.context, context);
	}
}
