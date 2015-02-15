package org.herac.tuxguitar.io.base;

import java.io.BufferedInputStream;
import java.util.Iterator;

import org.herac.tuxguitar.util.TGContext;

public class TGSongLoader {
	
	private TGContext context;
	
	public TGSongLoader(TGContext context){
		this.context = context;
	}
	
	public void load(TGSongLoaderHandle handle) throws TGFileFormatException{
		try{
			BufferedInputStream stream = new BufferedInputStream(handle.getInputStream());
			
			stream.mark(1);
			Iterator it = TGFileFormatManager.getInstance(this.context).getInputStreams();
			while(it.hasNext() && handle.getSong() == null){
				TGInputStreamBase reader = (TGInputStreamBase)it.next();
				reader.init(handle.getFactory(),stream);
				if( reader.isSupportedVersion() ){
					handle.setSong(reader.readSong());
					handle.setFormat(reader.getFileFormat());
					return;
				}
				stream.reset();
			}
			stream.close();
		} catch(Throwable t) {
			throw new TGFileFormatException(t);
		}
		throw new TGFileFormatException("Unsupported file format");
	}
}