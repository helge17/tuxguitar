package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class LilypondSongExporter implements TGLocalFileExporter{
	
	public static final String PROVIDER_ID = LilypondSongExporter.class.getName();
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
	
	public String getExportName() {
		return "Lilypond";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Lilypond", new String[]{"ly"});
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new LilypondSongStream(context);
	}
}