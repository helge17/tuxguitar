package org.herac.tuxguitar.community.io;

import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class TGShareSongExporter implements TGSongExporter { 
	
	private TGContext context;
	
	public TGShareSongExporter(TGContext context){
		this.context = context;
	}
	
	public String getProviderId() {
		return this.getClass().getName();
	}
	
	public String getExportName() {
		return ("Share with the Community");
	}

	public TGSongStream openStream(TGSongStreamContext streamContext) {
		return new TGShareSongStream(this.context, streamContext);
	}
}
