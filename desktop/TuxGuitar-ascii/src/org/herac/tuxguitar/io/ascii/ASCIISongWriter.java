package org.herac.tuxguitar.io.ascii;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;

public class ASCIISongWriter implements TGSongWriter {
	
	public ASCIISongWriter() {
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("ASCII", "text/x-tab", new String[]{"tab"});
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			new ASCIITabOutputStream(handle.getOutputStream()).writeSong(handle.getSong());
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}