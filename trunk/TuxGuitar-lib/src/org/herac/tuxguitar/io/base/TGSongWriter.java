package org.herac.tuxguitar.io.base;

import java.io.BufferedOutputStream;
import java.util.Iterator;

public class TGSongWriter {
	
	public TGSongWriter(){
		super();
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException{
		try {
			Iterator it = TGFileFormatManager.instance().getOutputStreams();
			while(it.hasNext()){
				TGOutputStreamBase writer = (TGOutputStreamBase)it.next();
				if( writer.getFileFormat().getName().equals(handle.getFormat().getName()) ){
					writer.init(handle.getFactory(), new BufferedOutputStream(handle.getOutputStream()));
					writer.writeSong(handle.getSong());
					return;
				}
			}
		} catch (Throwable t) {
			throw new TGFileFormatException(t);
		}
		throw new TGFileFormatException("Unsupported file format");
	}
}
