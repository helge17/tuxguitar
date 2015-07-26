package org.herac.tuxguitar.io.tg.v10;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class TGOutputStreamProvider implements TGLocalFileExporter{
	
	public String getProviderId() {
		return this.getExportName();
	}
	
	public String getExportName(){
		return "TuxGuitar 1.0";
	}
	
	public TGFileFormat getFileFormat(){
		return new TGFileFormat("TuxGuitar 1.0", new String[]{"tg"});
	}
	
	public TGSongStream openStream(TGSongStreamContext context) {
		return new TGOutputStream(context);
	}
}
