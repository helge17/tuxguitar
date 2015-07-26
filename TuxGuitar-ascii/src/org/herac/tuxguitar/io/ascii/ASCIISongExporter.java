package org.herac.tuxguitar.io.ascii;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class ASCIISongExporter implements TGLocalFileExporter{
	
	public String getProviderId() {
		return this.getClass().getName();
	}
	
	public String getExportName() {
		return "ASCII";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("ASCII", new String[]{"tab"});
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new ASCIISongStream(context);
	}
}