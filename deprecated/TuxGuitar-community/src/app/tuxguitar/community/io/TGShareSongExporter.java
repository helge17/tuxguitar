package app.tuxguitar.community.io;

import app.tuxguitar.io.base.TGSongExporter;
import app.tuxguitar.io.base.TGSongStream;
import app.tuxguitar.io.base.TGSongStreamContext;
import app.tuxguitar.util.TGContext;

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
