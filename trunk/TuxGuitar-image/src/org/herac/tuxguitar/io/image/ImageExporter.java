package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class ImageExporter implements TGRawExporter{
	
	public static final String PROVIDER_ID = ImageExporter.class.getName();
	
	public ImageExporter() {
		super();
	}
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
	
	public String getExportName() {
		return TuxGuitar.getProperty("tuxguitar-image.export-label");
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new ImageExporterStream(context);
	}
}
