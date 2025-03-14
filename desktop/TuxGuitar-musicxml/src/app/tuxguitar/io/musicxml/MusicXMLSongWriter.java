package app.tuxguitar.io.musicxml;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;

public class MusicXMLSongWriter implements TGSongWriter {

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("MusicXML", "application/vnd.recordare.musicxml+xml", new String[]{"musicxml"});

	public MusicXMLSongWriter() {
		super();
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			new MusicXMLWriter(handle.getOutputStream()).writeSong(handle.getSong());
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}