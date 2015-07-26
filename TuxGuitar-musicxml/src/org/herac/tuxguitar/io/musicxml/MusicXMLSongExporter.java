package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class MusicXMLSongExporter implements TGLocalFileExporter{
	
	public String getProviderId() {
		return this.getClass().getName();
	}
	
	public String getExportName() {
		return "MusicXML";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("MusicXML", new String[]{"xml"});
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new MusicXMLSongStream(context);
	}
}