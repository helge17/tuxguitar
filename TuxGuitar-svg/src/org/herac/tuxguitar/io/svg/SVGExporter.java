package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class SVGExporter implements TGLocalFileExporter {
	
	public static final String PROVIDER_ID = SVGExporter.class.getName();
	
	public SVGExporter(){
		super();
	}
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
	
	public String getExportName() {
		return "SVG";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Scalable Vector Graphics", new String[]{"svg"});
	}

	public TGSongStream openStream(TGSongStreamContext streamContext) {
		return new SVGExporterStream(streamContext);
	}
}
