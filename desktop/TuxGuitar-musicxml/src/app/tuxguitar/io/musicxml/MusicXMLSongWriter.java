package app.tuxguitar.io.musicxml;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.util.TGContext;

public class MusicXMLSongWriter implements TGSongWriter {

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("MusicXML", "application/vnd.recordare.musicxml+xml", new String[]{"musicxml"});

	private TGContext context;

	public MusicXMLSongWriter(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			new MusicXMLWriter(this.context, handle.getOutputStream()).writeSong(handle.getSong());
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}