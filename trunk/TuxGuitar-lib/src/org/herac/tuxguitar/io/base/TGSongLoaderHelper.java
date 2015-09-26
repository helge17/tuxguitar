package org.herac.tuxguitar.io.base;

import java.io.BufferedInputStream;
import java.util.Iterator;

import org.herac.tuxguitar.util.TGContext;

public class TGSongLoaderHelper {
	
	private TGContext context;
	
	public TGSongLoaderHelper(TGContext context){
		this.context = context;
	}
	
	public void load(TGSongLoaderHandle handle) throws TGFileFormatException{
		try{
			BufferedInputStream stream = new BufferedInputStream(handle.getInputStream());
			
			stream.mark(1);
			Iterator<TGInputStreamBase> it = TGFileFormatManager.getInstance(this.context).getInputStreams();
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
		} catch(Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
		throw new TGFileFormatException("Unsupported file format");
	}
}