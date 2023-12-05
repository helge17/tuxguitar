package org.herac.tuxguitar.io.musicxml;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;

public class MusicXMLSongWriter implements TGSongWriter {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("MusicXML", "application/vnd.recordare.musicxml+xml", new String[]{"xml"});
	
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