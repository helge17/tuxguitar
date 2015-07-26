package org.herac.tuxguitar.io.base;

import java.io.BufferedOutputStream;
import java.util.Iterator;

import org.herac.tuxguitar.util.TGContext;

public class TGSongWriterHelper {
	
	private TGContext context;
	
	public TGSongWriterHelper(TGContext context){
		this.context = context;
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException{
		try {
			Iterator<TGOutputStreamBase> it = TGFileFormatManager.getInstance(this.context).getOutputStreams();
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
