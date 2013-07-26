/*
 * Created on 19-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.io.base;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGSongLoader {
	
	public TGSongLoader(){
		super();
	}
	
	/**
	 * @return TGSong
	 * @throws TGFileFormatException
	 */
	public TGSong load(TGFactory factory,InputStream is) throws TGFileFormatException{
		try{
			BufferedInputStream stream = new BufferedInputStream(is);
			stream.mark(1);
			Iterator it = TGFileFormatManager.instance().getInputStreams();
			while(it.hasNext()){
				TGInputStreamBase reader = (TGInputStreamBase)it.next();
				reader.init(factory,stream);
				if(reader.isSupportedVersion()){
					return reader.readSong();
				}
				stream.reset();
			}
			stream.close();
		}catch(Throwable t){
			throw new TGFileFormatException(t);
		}
		throw new TGFileFormatException("Unsupported file format");
	}
}